package com.chylex.forge.mappinghints
import com.chylex.forge.mappinghints.CsvMappings.Entry
import com.chylex.forge.mappinghints.CsvMappings.Entry.Status.NOT_VALIDATED
import com.chylex.forge.mappinghints.CsvMappings.Entry.Status.SPECIAL
import com.chylex.forge.mappinghints.CsvMappings.Entry.Status.VALIDATED
import com.intellij.codeInsight.hints.InlayHintsCollector
import com.intellij.codeInsight.hints.InlayHintsSink
import com.intellij.codeInsight.hints.presentation.AttributesTransformerPresentation
import com.intellij.codeInsight.hints.presentation.InlayPresentation
import com.intellij.codeInsight.hints.presentation.InsetPresentation
import com.intellij.codeInsight.hints.presentation.PresentationFactory
import com.intellij.codeInsight.hints.presentation.SequencePresentation
import com.intellij.codeInsight.hints.presentation.SpacePresentation
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.intellij.ui.ColorUtil
import java.awt.Color

@Suppress("UnstableApiUsage")
class HintCollector(editor: Editor, private val settings: PluginSettings) : InlayHintsCollector{
	private val factory = PresentationFactory(editor as EditorImpl)
	private val mappings = CsvMappings(settings)
	
	private val colorValidatedGreen: Color
	private val colorValidatedBlue: Color
	private val colorNotValidated: Color
	
	init{
		val baseColor = editor.colorsScheme.getAttributes(DefaultLanguageHighlighterColors.INLINE_PARAMETER_HINT).foregroundColor
		
		colorValidatedGreen = ColorUtil.mix(baseColor, Color.GREEN, 0.2)
		colorValidatedBlue = ColorUtil.mix(baseColor, Color.CYAN, 0.2)
		colorNotValidated = ColorUtil.mix(baseColor, Color.YELLOW, 0.2)
	}
	
	override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean{
		val name = when(element){
			is PsiIdentifier -> element.text
			else -> null
		}
		
		val entry = name?.let(mappings::map) ?: return true
		val hint = createHint(entry)
		
		if (settings.displayHintsInline){
			sink.addInlineElement(element.endOffset, relatesToPrecedingText = true, presentation = InsetPresentation(hint, top = 3 /* TODO */, left = 1, right = 1))
		}
		else{
			val doc = editor.document
			val offset = element.startOffset
			val column = offset - doc.getLineStartOffset(doc.getLineNumber(offset))
			
			val presentation = SequencePresentation(listOf(
				SpacePresentation(column * EditorUtil.getPlainSpaceWidth(editor), 0),
				hint
			))
			
			sink.addBlockElement(
				offset = offset,
				showAbove = true,
				presentation = presentation,
				relatesToPrecedingText = false,
				priority = 100
			)
		}
		
		return true
	}
	
	private fun createHint(entry: Entry): InlayPresentation{
		val hint = factory.smallText(entry.mappedName)
		
		return when(entry.status){
			SPECIAL       -> hint
			VALIDATED     -> createForegroundColorPresentation(hint, if (settings.validatedColorBlue) colorValidatedBlue else colorValidatedGreen)
			NOT_VALIDATED -> createForegroundColorPresentation(hint, colorNotValidated)
		}
	}
	
	private fun createForegroundColorPresentation(wrapped: InlayPresentation, color: Color): InlayPresentation{
		return AttributesTransformerPresentation(wrapped){
			it.clone().apply { foregroundColor = color }
		}
	}
}

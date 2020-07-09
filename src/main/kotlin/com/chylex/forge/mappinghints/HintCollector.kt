package com.chylex.forge.mappinghints
import com.intellij.codeInsight.hints.InlayHintsCollector
import com.intellij.codeInsight.hints.InlayHintsSink
import com.intellij.codeInsight.hints.presentation.InsetPresentation
import com.intellij.codeInsight.hints.presentation.PresentationFactory
import com.intellij.codeInsight.hints.presentation.SequencePresentation
import com.intellij.codeInsight.hints.presentation.SpacePresentation
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset

@Suppress("UnstableApiUsage")
class HintCollector(editor: Editor, private val settings: PluginSettings) : InlayHintsCollector{
	private val factory = PresentationFactory(editor as EditorImpl)
	private val mappings = CsvMappings(settings)
	
	override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean{
		val name = when(element){
			is PsiIdentifier -> element.text
			else -> null
		}
		
		val translated = name?.let(mappings::map) ?: return true
		val hint = factory.smallText(translated)
		
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
}

package com.chylex.forge.mappinghints
import com.intellij.codeInsight.hints.ChangeListener
import com.intellij.codeInsight.hints.ImmediateConfigurable
import com.intellij.codeInsight.hints.ImmediateConfigurable.Case
import com.intellij.codeInsight.hints.InlayHintsCollector
import com.intellij.codeInsight.hints.InlayHintsProvider
import com.intellij.codeInsight.hints.InlayHintsSink
import com.intellij.codeInsight.hints.SettingsKey
import com.intellij.lang.Language
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.psi.PsiFile
import com.intellij.ui.layout.panel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

@Suppress("UnstableApiUsage")
class HintProvider : InlayHintsProvider<PluginSettings>{
	override val key = SettingsKey<PluginSettings>("chylexForgeMappingHints")
	
	override val name = "Minecraft Forge mapping suggestions"
	override val previewText: String? = null
	
	override fun createConfigurable(settings: PluginSettings): ImmediateConfigurable{
		return object : ImmediateConfigurable{
			override fun createComponent(listener: ChangeListener) = panel {
				row("Mappings:"){
					val csv = FileChooserDescriptorFactory.createSingleFileDescriptor("csv")
					val field = textFieldWithBrowseButton("Mappings (CSV)", settings.mappingFile, fileChooserDescriptor = csv).component.childComponent
					
					field.document.addDocumentListener(object : DocumentListener{
						override fun changedUpdate(e: DocumentEvent?) = update()
						override fun insertUpdate(e: DocumentEvent?) = update()
						override fun removeUpdate(e: DocumentEvent?) = update()
						
						private fun update(){
							settings.mappingFile = field.text
							listener.settingsChanged()
						}
					})
				}
			}
			
			override val cases
				get() = listOf(
					Case("Show missing", "show.missing", settings::showMissing),
					Case("Show empty", "show.empty", settings::showEmpty),
					Case("Show validated", "show.validated", settings::showValidated),
					Case("Validated names are blue instead of green", "display.validatedblue", settings::validatedColorBlue),
					Case("Hints are displayed inline", "display.hintsinline", settings::displayHintsInline)
				)
		}
	}
	
	override fun createSettings(): PluginSettings{
		return PluginSettings()
	}
	
	override fun getCollectorFor(file: PsiFile, editor: Editor, settings: PluginSettings, sink: InlayHintsSink): InlayHintsCollector?{
		return HintCollector(editor, settings)
	}
	
	override fun isLanguageSupported(language: Language): Boolean{
		return language is JavaLanguage
	}
}

package com.chylex.forge.mappinghints
import com.intellij.codeInsight.hints.ChangeListener
import com.intellij.codeInsight.hints.ImmediateConfigurable
import com.intellij.codeInsight.hints.InlayHintsCollector
import com.intellij.codeInsight.hints.InlayHintsProvider
import com.intellij.codeInsight.hints.InlayHintsSink
import com.intellij.codeInsight.hints.NoSettings
import com.intellij.codeInsight.hints.SettingsKey
import com.intellij.lang.Language
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.ui.layout.panel

@Suppress("UnstableApiUsage")
class HintProvider : InlayHintsProvider<NoSettings>{
	override val key = SettingsKey<NoSettings>("chylexForgeMappingHints")
	
	override val name = "Minecraft Forge Mapping Suggestions"
	override val previewText: String? = null
	
	override fun createConfigurable(settings: NoSettings): ImmediateConfigurable{
		return object : ImmediateConfigurable{
			override fun createComponent(listener: ChangeListener) = panel {}
		}
	}
	
	override fun createSettings(): NoSettings{
		return NoSettings()
	}
	
	override fun getCollectorFor(file: PsiFile, editor: Editor, settings: NoSettings, sink: InlayHintsSink): InlayHintsCollector?{
		return HintCollector(editor, useInlineHints = true)
	}
	
	override fun isLanguageSupported(language: Language): Boolean{
		return language is JavaLanguage
	}
}

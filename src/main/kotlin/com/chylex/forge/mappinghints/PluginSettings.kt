package com.chylex.forge.mappinghints

data class PluginSettings(
	var mappingFile: String = "",
	var showValidated: Boolean = true,
	var showMissing: Boolean = true,
	var showEmpty: Boolean = true,
	var displayHintsInline: Boolean = true
)

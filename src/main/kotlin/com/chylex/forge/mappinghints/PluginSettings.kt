package com.chylex.forge.mappinghints

data class PluginSettings(
	var mappingFile: String = "",
	var showMissing: Boolean = true,
	var showEmpty: Boolean = true,
	var showValidated: Boolean = true,
	var validatedColorBlue: Boolean = false,
	var displayHintsInline: Boolean = true
)

package com.chylex.forge.mappinghints
import java.io.File

class CsvMappings(file: File){
	private val entries = mutableListOf<Entry>()
	private val mappings = mutableMapOf<String, String>()
	
	init{
		for(line in file.readLines().drop(1).map { it.split(',', limit = 5) }.filter { it.size >= 5 }){
			val entry = Entry(line[0] == "TRUE", line[1], line[2], line[3], line[4])
			
			entries.add(entry)
			mappings[entry.unmappedName] = entry.mappedName
		}
	}
	
	fun map(unmappedName: String) = if (isSrgName(unmappedName))
		mappings[unmappedName]?.ifEmpty { "<empty>" } ?: "<missing>"
	else
		null
	
	private fun isSrgName(name: String): Boolean{
		return name.startsWith("field_") || name.startsWith("func_") || name.startsWith("p_")
	}
	
	data class Entry(
		val isChecked: Boolean,
		val className: String,
		val unmappedName: String,
		val mappedName: String,
		val comment: String
	)
}

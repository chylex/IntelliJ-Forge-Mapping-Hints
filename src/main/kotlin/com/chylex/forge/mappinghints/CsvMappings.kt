package com.chylex.forge.mappinghints
import com.intellij.util.text.nullize
import java.io.File

class CsvMappings(private val settings: PluginSettings){
	private val entries = mutableListOf<Entry>()
	private val mappings = mutableMapOf<String, Entry>()
	
	init{
		val file = settings.mappingFile.nullize(nullizeSpaces = true)?.let(::File)
		val lines = file?.readLines().orEmpty().drop(1).map { it.split(',', limit = 5) }.filter { it.size >= 4 }
		
		for(line in lines){
			val entry = Entry(line[0] == "TRUE", line[1], line[2], line[3])
			
			entries.add(entry)
			mappings[entry.unmappedName] = entry
		}
	}
	
	fun map(unmappedName: String): String?{
		if (!isSrgName(unmappedName)){
			return null
		}
		
		val entry = mappings[unmappedName]
		
		return if (entry == null){
			if (settings.showMissing) "<missing>" else null
		}
		else if (entry.isEmpty){
			if (settings.showEmpty) "<empty>" else null
		}
		else{
			if (settings.showValidated || !entry.isValidated) entry.mappedName else null
		}
	}
	
	private fun isSrgName(name: String): Boolean{
		return name.startsWith("field_") || name.startsWith("func_") || name.startsWith("p_")
	}
	
	data class Entry(
		val isValidated: Boolean,
		val className: String,
		val unmappedName: String,
		val mappedName: String
	){
		val isEmpty = mappedName.isEmpty()
	}
}

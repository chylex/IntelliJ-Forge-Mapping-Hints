package com.chylex.forge.mappinghints
import com.chylex.forge.mappinghints.CsvMappings.Entry.Companion.empty
import com.chylex.forge.mappinghints.CsvMappings.Entry.Companion.missing
import com.chylex.forge.mappinghints.CsvMappings.Entry.Status.NOT_VALIDATED
import com.chylex.forge.mappinghints.CsvMappings.Entry.Status.SPECIAL
import com.chylex.forge.mappinghints.CsvMappings.Entry.Status.VALIDATED
import com.intellij.util.text.nullize
import java.io.File

class CsvMappings(private val settings: PluginSettings){
	private val entries = mutableListOf<Entry>()
	private val mappings = mutableMapOf<String, Entry>()
	
	init{
		val file = settings.mappingFile.nullize(nullizeSpaces = true)?.let(::File)
		val lines = file?.readLines().orEmpty().drop(1).map { it.split(',', limit = 5) }.filter { it.size >= 4 }
		
		for(line in lines){
			val entry = Entry(
				isValidated  = line[0] == "TRUE",
				className    = line[1],
				unmappedName = line[2],
				mappedName   = line[3]
			)
			
			entries.add(entry)
			mappings[entry.unmappedName] = entry
		}
	}
	
	fun map(unmappedName: String): Entry?{
		if (!isSrgName(unmappedName)){
			return null
		}
		
		val entry = mappings[unmappedName]
		
		return if (entry == null){
			if (settings.showMissing) missing(unmappedName) else null
		}
		else if (entry.isEmpty){
			if (settings.showEmpty) empty(unmappedName) else null
		}
		else{
			if (settings.showValidated || entry.status != VALIDATED) entry else null
		}
	}
	
	private fun isSrgName(name: String): Boolean{
		return name.startsWith("field_") || name.startsWith("func_") || name.startsWith("p_")
	}
	
	@Suppress("DataClassPrivateConstructor")
	data class Entry private constructor(
		val status: Status,
		val isEmpty: Boolean,
		val className: String,
		val unmappedName: String,
		val mappedName: String
	){
		constructor(isValidated: Boolean, className: String, unmappedName: String, mappedName: String) : this(
			status       = if (isValidated) VALIDATED else NOT_VALIDATED,
			isEmpty      = mappedName.isEmpty(),
			className    = className,
			unmappedName = unmappedName,
			mappedName   = mappedName
		)
		
		constructor(unmappedName: String, displayName: String) : this(
			status       = SPECIAL,
			isEmpty      = true,
			className    = "",
			unmappedName = unmappedName,
			mappedName   = displayName
		)
		
		companion object{
			fun missing(unmappedName: String) = Entry(unmappedName = unmappedName, displayName = "<missing>")
			fun empty(unmappedName: String)   = Entry(unmappedName = unmappedName, displayName = "<empty>")
		}
		
		enum class Status{
			SPECIAL, VALIDATED, NOT_VALIDATED
		}
	}
}

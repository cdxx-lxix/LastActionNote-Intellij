package com.mladich.lastactionnote.tools


import com.intellij.openapi.project.Project
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CommonData {
    // Place to store some useful data across classes
    companion object {
        const val pluginFileName = ".lastactionnote"
        val myBundle: ResourceBundle = ResourceBundle.getBundle("Translations")
        var currentDate: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        var noteSaved: Boolean = false
        var projectInstance: Project? = null
        var exitPlease: Boolean = true
    }
}
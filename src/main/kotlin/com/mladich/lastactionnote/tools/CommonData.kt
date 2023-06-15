package com.mladich.lastactionnote.tools


import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CommonData {
    companion object {
        const val pluginFileName = ".lastactionnote"
        val myBundle: ResourceBundle = ResourceBundle.getBundle("Translations")
        var currentDate: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    }
}
package com.mladich.lastactionnote.tools


import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class CommonData {
    // Place to store some useful data across classes
    companion object {
        // Unchangeable name of the file
        const val pluginFileName = ".lastactionnote"
        // Translation package
        val myBundle: ResourceBundle = ResourceBundle.getBundle("Translations")
        // Date-time specific to the user's system format
        var currentDate: String = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locale.getDefault()).format(LocalDateTime.now());
        // Used to show note dialog only once
        var noteSaved: Boolean = false
    }
}
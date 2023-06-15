package com.mladich.lastactionnote.tools


import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CommonData {
    companion object {
        val myBundle: ResourceBundle = ResourceBundle.getBundle("Translations")
        var currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    }
}
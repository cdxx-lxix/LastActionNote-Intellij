package com.mladich.lastactionnote.tools


import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.mladich.lastactionnote.settings.LANSettingsService
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class CommonData {
    // Place to store some useful data across classes
    companion object {
        // Unchangeable name of the note file
        const val pluginFileName = ".lastactionnote"
        // Unchangeable name of the settings file
        const val pluginSettingsFileName = "lastactionnote_settings.xml"
        // Translation package
        val myBundle: ResourceBundle = ResourceBundle.getBundle("Translations")
        // Date-time specific to the user's system format
        var currentDate: String = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locale.getDefault()).format(LocalDateTime.now())
        // Map for projects
        val openedProjects: Object2ObjectOpenHashMap<Project, ProjectData> = Object2ObjectOpenHashMap()
        // Data associated with a project
        class ProjectData (var isNoteSaved: Boolean, var fileHistory: MutableList<String>, var fileCounter: Int, var isExcluded: Boolean)
        // File history service instance
        val history = service<FileHistory>()
        // Plugin settings service instance
        val pluginSettingsService: LANSettingsService = ApplicationManager.getApplication().getService(LANSettingsService::class.java)
    }
}
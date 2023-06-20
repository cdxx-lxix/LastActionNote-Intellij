package com.mladich.lastactionnote.settings

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.util.xmlb.XmlSerializerUtil
import com.mladich.lastactionnote.tools.CommonData.Companion.openedProjects
import com.mladich.lastactionnote.tools.CommonData.Companion.pluginSettingsFileName
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable


data class PluginSettings (
    // Container for settings
    var projectExclusion: Boolean = false
)

class ToggleProjectExclusionAction : ToggleAction() {
    override fun getActionUpdateThread(): ActionUpdateThread {
        // Event Driven Task. Updates when action is performed instead of running in the background
        return ActionUpdateThread.EDT
    }
    override fun isSelected(e: AnActionEvent): Boolean {
        val project = e.project ?: return false
        val settingsService = project.service<LANSettingsService>()
        return settingsService.state.projectExclusion
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        val project = e.project ?: return
        val settingsService = project.service<LANSettingsService>()
        settingsService.state.projectExclusion = state // Update service
        openedProjects[project]!!.isExcluded = state // Update project data
    }
}

@State(
    // Do not change
    name = "ProjectExclusion",
    storages = [Storage(pluginSettingsFileName)] // XML file name
)
class LANSettingsService : PersistentStateComponent<PluginSettings> {
    private var mySettings: PluginSettings = PluginSettings()

    @Nullable
    override fun getState(): PluginSettings {
        return mySettings
    }

    override fun loadState(@NotNull state: PluginSettings) {
        XmlSerializerUtil.copyBean(state, this.state)
    }
}
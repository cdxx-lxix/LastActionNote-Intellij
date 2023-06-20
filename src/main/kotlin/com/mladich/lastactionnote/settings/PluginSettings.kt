package com.mladich.lastactionnote.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import com.mladich.lastactionnote.tools.CommonData.Companion.pluginSettingsFileName
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable


data class PluginSettings (
    // Container for settings
    var projectExclusion: Boolean = false,
    var excludedProjects: MutableSet<String> = mutableSetOf()
)

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
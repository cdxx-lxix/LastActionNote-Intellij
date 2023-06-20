package com.mladich.lastactionnote.tools

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.mladich.lastactionnote.tools.CommonData.Companion.openedProjects

@Service
class FileHistory {
    private val maxSize = 5
    fun getCounter(project: Project): Int {
        val currentProject = openedProjects[project]
        return currentProject!!.fileCounter
    }
    fun increaseCounter(project: Project) {
        val currentProject = openedProjects[project]
        currentProject!!.fileCounter++
    }
    fun getHistory(project: Project): List<String> {
        val currentProject = openedProjects[project]
        return currentProject!!.fileHistory.toList()
    }
    fun addToHistory(file: VirtualFile, project: Project) {
        val currentProject = openedProjects[project]
        currentProject!!.fileHistory.remove(file.name) // Remove the file if it already exists in the list
        currentProject.fileHistory.add(file.name) // Add the file to the beginning of the list
        if (currentProject.fileHistory.size > maxSize) {
            currentProject.fileHistory.removeFirst() // Keep the list size limited to maxSize
        }
    }
}
package com.mladich.lastactionnote.listeners

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.VetoableProjectManagerListener
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.mladich.lastactionnote.dialogs.CloseNoteDialog
import com.mladich.lastactionnote.dialogs.OpenNoteDialog
import com.mladich.lastactionnote.tools.CommonData
import com.mladich.lastactionnote.tools.CommonData.Companion.history
import com.mladich.lastactionnote.tools.CommonData.Companion.openedProjects
import com.mladich.lastactionnote.tools.CommonData.Companion.pluginSettingsService
import com.mladich.lastactionnote.tools.FileHistory
import org.jetbrains.annotations.NotNull

class OpeningProjectListener : StartupActivity {
    private fun showDialog(project: Project, currentClosingProject: CommonData.Companion.ProjectData): Boolean {
        val dialogWindow = CloseNoteDialog(project)
        dialogWindow.showAndGet()
        // OK exit code - 0, Cancel\Close exit code - 1
        return if (dialogWindow.exitCode == 1) false else {
            currentClosingProject.isNoteSaved = true
            true
        }
    }
    fun checkConditions(@NotNull project: Project): Boolean {
        val currentClosingProject = openedProjects[project] ?: return false
        return when {
            exclusionCheck(project) -> true // When excluded - close
            currentClosingProject.isNoteSaved -> true // When not excluded and note is saved - close
            else -> showDialog(project, currentClosingProject) // When NOT excluded and NOT saved - show dialog
        }
    }
    private fun exclusionCheck(project: Project): Boolean {
        return pluginSettingsService.state.excludedProjects.contains(project.name)
    }
    override fun runActivity(@NotNull project: Project) {
        // Adds current instance to the map if it's not already present
        if (!openedProjects.containsKey(project)) {
            openedProjects[project] =
                CommonData.Companion.ProjectData(isNoteSaved = false,
                    fileHistory = mutableListOf(), fileCounter = 0,
                    isExcluded = exclusionCheck(project))
        }

        // Anonymous listener that intercepts X-button behaviour and allows cancel of IDE closing
        val parentDisposable: Disposable = Disposer.newDisposable()
        ApplicationManager.getApplication().addApplicationListener(object : ApplicationListener {
            override fun canExitApplication(): Boolean {
                return checkConditions(project)

            }
        }, parentDisposable )
        // Anonymous listener that intercepts "close project" behaviour and allows cancel of project closing
        ProjectManager.getInstance().addProjectManagerListener(object : VetoableProjectManagerListener {
            override fun canClose(project: Project): Boolean {
                return checkConditions(project)
            }
        })
        // Checks the settings for exclusion. Starts services if it's FALSE (not excluded)
        if (!openedProjects[project]!!.isExcluded) {
            val dialogWindow = OpenNoteDialog(project)
            dialogWindow.showAndGet()
            project.service<FileHistory>() // Start history writing
        }
    }
}

class FileListener : FileEditorManagerListener {
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        source.project
        val document = FileDocumentManager.getInstance().getDocument(file)

        if (document != null) {
            val disposable = Disposer.newDisposable()
            val documentListener = object : DocumentListener {
                override fun documentChanged(event: DocumentEvent) {
                    // Remove the listener and dispose the disposable
                    document.removeDocumentListener(this)
                    Disposer.dispose(disposable)

                    // Add the file to history and increase counter
                    history.addToHistory(file, source.project)
                    history.increaseCounter(source.project)
                }
            }
            document.addDocumentListener(documentListener, disposable)
        }
    }
}
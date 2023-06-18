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
import com.mladich.lastactionnote.tools.CommonData.Companion.noteSaved
import com.mladich.lastactionnote.tools.FileHistory
import org.jetbrains.annotations.NotNull


val history = service<FileHistory>()
class OpeningProjectListener : StartupActivity {
    fun showDialog(@NotNull project: Project): Boolean {
        return if (!noteSaved) {
            val dialogWindow = CloseNoteDialog(project)
            dialogWindow.showAndGet()
            // OK exit code - 0, Cancel\Close exit code - 1
            if (dialogWindow.exitCode == 1) {
                false
            } else {
                noteSaved = true
                true
            }
        } else {
            true
        }
    }
    override fun runActivity(@NotNull project: Project) {
        // This ugly monstrosity intercepts X-button behaviour and allows cancel of IDE closing
        val parentDisposable: Disposable = Disposer.newDisposable()
        ApplicationManager.getApplication().addApplicationListener(object : ApplicationListener {
            override fun canExitApplication(): Boolean {
                return showDialog(project)
            }
        }, parentDisposable )
        // This ugly monstrosity intercepts "close project" behaviour and allows cancel of project closing
        ProjectManager.getInstance().addProjectManagerListener(object : VetoableProjectManagerListener {
            override fun canClose(project: Project): Boolean {
                return showDialog(project)
            }
        })
        val dialogWindow = OpenNoteDialog(project)
        dialogWindow.showAndGet()
        noteSaved = false // Reset value to show dialogs
        project.service<FileHistory>() // Start history writing
        history.clearHistoryAndCounter() // Clear previous history
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
                    history.addToHistory(file)
                    history.increaseCounter()
                }
            }
            document.addDocumentListener(documentListener, disposable)
        }
    }
}
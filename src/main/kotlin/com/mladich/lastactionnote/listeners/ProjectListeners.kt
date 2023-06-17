package com.mladich.lastactionnote.listeners

import com.intellij.ide.AppLifecycleListener
import com.intellij.openapi.application.ApplicationListener
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.mladich.lastactionnote.dialogs.CloseNoteDialog
import com.mladich.lastactionnote.dialogs.OpenNoteDialog
import com.mladich.lastactionnote.tools.CommonData.Companion.exitPlease
import com.mladich.lastactionnote.tools.CommonData.Companion.noteSaved
import com.mladich.lastactionnote.tools.CommonData.Companion.projectInstance
import com.mladich.lastactionnote.tools.FileHistory
import org.jetbrains.annotations.NotNull


val history = service<FileHistory>()
class ClosingProjectListener : ProjectManagerListener {

    override fun projectClosingBeforeSave(project: Project) {
        if (!noteSaved) {
            val dialogWindow = CloseNoteDialog(project)
            dialogWindow.showAndGet()
            noteSaved = true
        }
    }
}
class CrossButtonListener: AppLifecycleListener {
    override fun appClosing() {
        super.appClosing()
        if (projectInstance != null) {
            val dialogWindow = CloseNoteDialog(projectInstance!!)
            dialogWindow.showAndGet()
            noteSaved = true
        }
    }
}

class CanExit :  ApplicationListener {
    override fun canExitApplication(): Boolean {
        super.canExitApplication()
        println("Can I exit please?" + exitPlease)
        return exitPlease
    }
}

class OpeningProjectListener : StartupActivity {
    override fun runActivity(@NotNull project: Project) {
        projectInstance = project
        noteSaved = false
        val dialogWindow = OpenNoteDialog(project)
        dialogWindow.showAndGet()
        project.service<FileHistory>()
        history.clearHistoryAndCounter()
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
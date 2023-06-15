package com.mladich.lastactionnote.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.vcs.changes.shelf.ShelveChangesManager.PostStartupActivity
import com.intellij.openapi.vfs.VirtualFile
import com.mladich.lastactionnote.dialogs.CloseNoteDialog
import com.mladich.lastactionnote.dialogs.OpenNoteDialog
import com.mladich.lastactionnote.tools.FileHistory
import org.jetbrains.annotations.NotNull

val history = service<FileHistory>()
class ClosingProjectListener : PostStartupActivity(), ProjectManagerListener {
    override fun projectClosing(project: Project) {
        val dialogWindow = CloseNoteDialog(project)
        dialogWindow.setFileListData(history.getHistory())
        if (dialogWindow.showAndGet()) {
            dialogWindow.doOKAction()
        }
    }
}


class OpeningProjectListener : StartupActivity {
    override fun runActivity(@NotNull project: Project) {
        val dialogWindow = OpenNoteDialog(project)
        dialogWindow.showMessage()
        project.service<FileHistory>()
    }
}

class FileListener: FileEditorManagerListener {
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        history.addToHistory(file)
        history.increaseCounter()
    }
}
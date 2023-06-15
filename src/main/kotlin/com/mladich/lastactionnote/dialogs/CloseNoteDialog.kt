package com.mladich.lastactionnote.dialogs

import com.intellij.AbstractBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.*
import com.mladich.lastactionnote.listeners.history
import com.mladich.lastactionnote.tools.CommonData.Companion.currentDate
import com.mladich.lastactionnote.tools.CommonData.Companion.myBundle
import com.mladich.lastactionnote.tools.NoteManipulator
import java.awt.Toolkit
import javax.swing.JComponent


class CloseNoteDialog(private val project: Project) : DialogWrapper(true) {
    private var lastThingsField = ""

    init {
        isOKActionEnabled = true
        setOKButtonText(AbstractBundle.message(myBundle, "close.OKButton"))
        setCancelButtonText(AbstractBundle.message(myBundle, "close.CancelButton"))
        isResizable = false
        title = AbstractBundle.message(myBundle, "close.DialogTitle")
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        setSize((screenSize.width / 6).coerceAtLeast(200), (screenSize.height / 6).coerceAtLeast(200))
        init()
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            row(AbstractBundle.message(myBundle, "close.dateLabel")) {
                label(currentDate) // Replace with your Date string
            }
            row(AbstractBundle.message(myBundle, "close.filesLabel")) {
                textArea().applyToComponent{
                    text = history.getHistory().joinToString(separator = "\n")
                    isEditable = false
                    emptyText.text = AbstractBundle.message(myBundle, "close.noFiles")
                }
            }.layout(RowLayout.PARENT_GRID).rowComment(AbstractBundle.message(myBundle, "close.filesTooltip") + " " + history.getCounter().toString())
            row {
                textArea()
                    .label(AbstractBundle.message(myBundle, "close.noteLabel"), LabelPosition.TOP)
                    .bindText(::lastThingsField)
                    .rows(10)
                    .columns(30)
                    .focused()
                    .applyToComponent{
                        emptyText.text = AbstractBundle.message(myBundle, "close.messageText")
                    }

            }.layout(RowLayout.PARENT_GRID).rowComment(AbstractBundle.message(myBundle, "close.noteTooltip"))

        }
    }

    public override fun doOKAction() {
        println("OK ACTION") // TODO: REMOVE ON PRODUCTION
        val files = history.getHistory()
        val noteManipulator = NoteManipulator()
        noteManipulator.saveData(lastThingsField, files, project)
        super.doOKAction()
    }

    override fun doCancelAction() {
        println("CANCEL ACTION") // TODO: REMOVE ON PRODUCTION
        NoteManipulator().setEmptyNote(project)
        super.doCancelAction()
    }
}
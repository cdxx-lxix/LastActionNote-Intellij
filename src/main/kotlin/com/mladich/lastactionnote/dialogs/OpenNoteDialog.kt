package com.mladich.lastactionnote.dialogs

import com.intellij.AbstractBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.*
import com.mladich.lastactionnote.tools.CommonData.Companion.myBundle
import com.mladich.lastactionnote.tools.NoteManipulator
import java.awt.Toolkit
import javax.swing.Action
import javax.swing.JComponent

class OpenNoteDialog (private val project: Project): DialogWrapper(false) {
    private val data = NoteManipulator().openData(project)
    init {
        isOKActionEnabled = true
        setOKButtonText(AbstractBundle.message(myBundle, "open.OKButton"))
        isResizable = false
        title = AbstractBundle.message(myBundle, "open.DialogTitle")
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        setSize((screenSize.width / 6).coerceAtLeast(200), (screenSize.height / 6).coerceAtLeast(200))
        init()
    }
    override fun createActions(): Array<Action> {
        // Removes every other button except for OK
        return arrayOf(okAction)
    }
    override fun createCenterPanel(): JComponent {
        return panel {
            row(AbstractBundle.message(myBundle, "open.dateLabel")) {
                label(data.date) // Replace with your Date string
            }
            row(AbstractBundle.message(myBundle, "close.filesLabel")) {
                textArea().applyToComponent{
                    text = data.files!!.joinToString(separator = "\n")
                    isEditable = false
                    emptyText.text = AbstractBundle.message(myBundle, "close.noFiles")
                }
            }.layout(RowLayout.PARENT_GRID).rowComment(AbstractBundle.message(myBundle, "close.filesTooltip") + " " + data.fileCounter)
            row {
                textArea()
                    .label(AbstractBundle.message(myBundle, "close.noteLabel"), LabelPosition.TOP)
                    .rows(10)
                    .columns(30)
                    .focused()
                    .applyToComponent{
                        text = data.text
                        isEditable = false
                    }

            }
        }
    }
}
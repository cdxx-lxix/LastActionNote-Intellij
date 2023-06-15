package com.mladich.lastactionnote.dialogs

import com.intellij.AbstractBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.ui.dsl.builder.*
import com.mladich.lastactionnote.listeners.history
import com.mladich.lastactionnote.tools.CommonData
import com.mladich.lastactionnote.tools.CommonData.Companion.myBundle
import com.mladich.lastactionnote.tools.NoteManipulator
import java.awt.Dimension
import java.awt.Toolkit
import java.util.*
import javax.swing.*

class OpenNoteDialog (private val project: Project): DialogWrapper(false) {
    private val data = NoteManipulator().openData(project)
    init {
        isOKActionEnabled = true
        setOKButtonText(AbstractBundle.message(myBundle, "close.OKButton"))
        isResizable = false
        title = AbstractBundle.message(myBundle, "close.DialogTitle")
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        setSize((screenSize.width / 6).coerceAtLeast(200), (screenSize.height / 6).coerceAtLeast(200))
        init()
    }
    override fun createCenterPanel(): JComponent {
        return panel {
            row(AbstractBundle.message(myBundle, "close.dateLabel")) {
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

            }.layout(RowLayout.PARENT_GRID).rowComment(AbstractBundle.message(myBundle, "close.noteTooltip"))

        }
    }
}
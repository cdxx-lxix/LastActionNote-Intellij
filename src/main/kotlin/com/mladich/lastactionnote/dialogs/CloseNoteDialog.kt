package com.mladich.lastactionnote.dialogs

import com.intellij.AbstractBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.*
import com.mladich.lastactionnote.tools.CommonData.Companion.currentDate
import com.mladich.lastactionnote.tools.CommonData.Companion.history
import com.mladich.lastactionnote.tools.CommonData.Companion.myBundle
import com.mladich.lastactionnote.tools.NoteManipulator
import java.awt.Dimension
import java.awt.Toolkit
import javax.swing.JComponent
import javax.swing.JSeparator

class CloseNoteDialog(private val project: Project) : DialogWrapper(true) {
    private var lastThingsField = "" // Puts user's data here on doOKAction. See line 45 to see the bind.

    init {
        isOKActionEnabled = true
        setOKButtonText(AbstractBundle.message(myBundle, "close.OKButton"))
        setCancelButtonText(AbstractBundle.message(myBundle, "close.CancelButton"))
        isResizable = false
        title = AbstractBundle.message(myBundle, "title.DialogTitle") + " " + project.name
        // Scales dialog window with user's screen size but makes it minimum of 200x220
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        setSize((screenSize.width / 6).coerceAtLeast(200), (screenSize.height / 6).coerceAtLeast(220))
        init()
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            // Timestamp
            row(AbstractBundle.message(myBundle, "close.dateLabel")) {
                label(currentDate) // Gets current date from CommonData
            }
            // File history
            row{
                textArea().applyToComponent{
                    text = history.getHistory(project).joinToString(separator = "\n") // Gets history from CommonData
                    isEditable = false // Disable edit
                    emptyText.text = AbstractBundle.message(myBundle, "close.noFiles") // Sets a default empty message if the user didn't edit anything
                }
                    .columns(30)
            }.layout(RowLayout.PARENT_GRID).rowComment(AbstractBundle.message(myBundle, "close.filesTooltip") + " " + history.getCounter(project).toString())
            // Note text field
            row {
                textArea()
                    .label(AbstractBundle.message(myBundle, "close.noteLabel"), LabelPosition.TOP)
                    .bindText(::lastThingsField) // Connects input data with my variable
                    .rows(10)
                    .columns(30)
                    .focused() // Puts the caret in it
                    .applyToComponent{
                        // Sets a default empty message
                        emptyText.text = AbstractBundle.message(myBundle, "close.messageText")
                    }

            }.layout(RowLayout.PARENT_GRID).rowComment(AbstractBundle.message(myBundle, "close.noteTooltip")) // Text under the field
            // Bottom line separator
            row {
                val separator = JSeparator()
                // This mofo is always smaller than the window for 20px IDK
                separator.minimumSize = Dimension(window.width + 20, 2)
                cell(separator)
            }.layout(RowLayout.PARENT_GRID)
        }
    }

    public override fun doOKAction() {
        super.doOKAction()
        /* super.do---Action MUST BE THE FIRST IN ORDER FOR THIS ACTION TO BE PERFORMED */
        // Save data and close
        val files = history.getHistory(project)
        val noteManipulator = NoteManipulator()
        noteManipulator.saveData(lastThingsField, files, project)
    }

    override fun doCancelAction() {
        // Cancel exit/project close
        super.doCancelAction()
    }
}
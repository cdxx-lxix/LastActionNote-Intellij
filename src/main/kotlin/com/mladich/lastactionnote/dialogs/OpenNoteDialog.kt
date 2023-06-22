package com.mladich.lastactionnote.dialogs

import com.intellij.AbstractBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.*
import com.mladich.lastactionnote.tools.CommonData
import com.mladich.lastactionnote.tools.CommonData.Companion.myBundle
import com.mladich.lastactionnote.tools.CommonData.Companion.pluginSettingsService
import com.mladich.lastactionnote.tools.NoteManipulator
import java.awt.Dimension
import java.awt.Toolkit
import javax.swing.Action
import javax.swing.JComponent
import javax.swing.JSeparator

class OpenNoteDialog (project: Project): DialogWrapper(false) {
    private val data = NoteManipulator().openData(project)
    private var checkbox: Boolean = false
    private val myProject = project
    private val screenSize = Toolkit.getDefaultToolkit().screenSize

    init {
        isOKActionEnabled = true
        setOKButtonText(AbstractBundle.message(myBundle, "open.OKButton"))
        isResizable = false
        title = AbstractBundle.message(myBundle, "title.DialogTitle") + " " + myProject.name
        setSize((screenSize.width / 6).coerceAtLeast(200), (screenSize.height / 6).coerceAtLeast(200))
        init()
    }
    override fun createActions(): Array<Action> {
        // Removes every other button except for OK
        return arrayOf(okAction)
    }
    override fun createCenterPanel(): JComponent {
        return panel {
            // Timestamp
            row(AbstractBundle.message(myBundle, "open.dateLabel")) {
                label(data.date)
            }
            // File history
            row{
                textArea().applyToComponent{
                    text = data.files!!.joinToString(separator = "\n")
                    isEditable = false
                    emptyText.text = AbstractBundle.message(myBundle, "close.noFiles")
                }
                    .columns(30)
            }.layout(RowLayout.PARENT_GRID).rowComment(AbstractBundle.message(myBundle, "close.filesTooltip") + " " + data.fileCounter)
            // Note text field
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
            // Exclusion option checkbox
            row{
                checkBox(AbstractBundle.message(myBundle, "open.exclusionCheckbox"))
                    .bindSelected(::checkbox)
            }
            // Bottom line separator
            row {
                val separator = JSeparator()
                // This mofo is always smaller than the window for 20px IDK
                separator.minimumSize = Dimension(window.width + 20, 2)
                cell(separator)
            }.layout(RowLayout.PARENT_GRID)
        }
    }

    override fun doOKAction() {
        super.doOKAction()
        // If a user ticks a checkbox changes settings value for exclusion
        if(checkbox) {
            pluginSettingsService.state.excludedProjects.add(myProject.name)
            CommonData.openedProjects[myProject]!!.isExcluded = checkbox
        }
    }
}
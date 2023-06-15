package com.mladich.lastactionnote.dialogs

import com.intellij.AbstractBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.panels.VerticalLayout
import com.mladich.lastactionnote.listeners.history
import com.mladich.lastactionnote.tools.CommonData.Companion.myBundle
import com.mladich.lastactionnote.tools.NoteManipulator
import java.awt.Toolkit
import java.util.*
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.ListSelectionModel


class CloseNoteDialog(private val project: Project) : DialogWrapper(true) {
    private var fileList: JBList<String>? = null
    private var lastThingsField = JBTextArea()

    init {
        isOKActionEnabled = true
        setCancelButtonText("Cancel")
        isResizable = false
        title = AbstractBundle.message(myBundle, "close.DialogTitle")
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        setSize((screenSize.width / 6).coerceAtLeast(200), (screenSize.height / 6).coerceAtLeast(200))
        init()
    }

    override fun createCenterPanel(): JComponent {
        lastThingsField.emptyText.setText(AbstractBundle.message(myBundle, "close.messageText"))
        lastThingsField.rows = 10
        lastThingsField.columns = 30
        lastThingsField.lineWrap = true
        lastThingsField.wrapStyleWord = true
        lastThingsField.toolTipText = AbstractBundle.message(myBundle, "close.messageTooltip")
        val lastThingsScrollPane = JBScrollPane(lastThingsField)
        fileList = JBList()
        fileList!!.selectionMode = ListSelectionModel.SINGLE_SELECTION
        fileList!!.isEnabled = false
        val centerPanel = JPanel(VerticalLayout(10))
        centerPanel.add(lastThingsScrollPane)
        centerPanel.add(fileList)
        return centerPanel
    }

    public override fun doOKAction() {
        val text = lastThingsField.text
        val files = history.getHistory()
        val noteManipulator = NoteManipulator()
        noteManipulator.saveData(text, files, project)
        super.doOKAction()
    }

    fun setFileListData(data: List<String>) {
        println("OnCloseDialog: received data: $data") // Debug. Remove on production
        fileList!!.setListData(data.toTypedArray<String>())
    }
}
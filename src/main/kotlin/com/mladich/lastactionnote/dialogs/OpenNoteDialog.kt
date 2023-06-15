package com.mladich.lastactionnote.dialogs

import com.intellij.AbstractBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.panels.VerticalLayout
import com.mladich.lastactionnote.tools.CommonData.Companion.myBundle
import com.mladich.lastactionnote.tools.NoteManipulator
import java.awt.Dimension
import java.awt.Toolkit
import java.util.*
import javax.swing.*

class OpenNoteDialog (private val project: Project) {
    fun showMessage() {
        val data = NoteManipulator().openData(project)
        val messagePanel = JPanel()
        messagePanel.layout = BoxLayout(messagePanel, BoxLayout.Y_AXIS)

        val messageLabel = JLabel("Text: ${data.text}")
        messagePanel.add(messageLabel)

        val separator = JSeparator(SwingConstants.HORIZONTAL)
        separator.maximumSize = Dimension(Integer.MAX_VALUE, 2)
        messagePanel.add(separator)

        val filesLabel = JLabel("<html>Files:<br>${data.files?.joinToString(separator = "<br>")}</html>")
        messagePanel.add(filesLabel)

        val frame = JFrame()
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        JOptionPane.showMessageDialog(frame, messagePanel, "Open Note", JOptionPane.INFORMATION_MESSAGE)
    }
}

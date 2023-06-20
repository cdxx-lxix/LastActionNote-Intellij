package com.mladich.lastactionnote.settings

import com.intellij.ide.RecentProjectListActionProvider
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import com.intellij.ui.BooleanTableCellRenderer
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.SwingConstants
import javax.swing.table.DefaultTableModel

class SettingsPanel : Configurable {
    private val panel = JPanel(BorderLayout())
    private lateinit var tableModel: DefaultTableModel
    private val recentProjectsActions = RecentProjectListActionProvider.getInstance().getActions(false, false)
    private val projects = recentProjectsActions.mapNotNull { (it as? AnAction)?.templatePresentation?.text }.toTypedArray()

    init {
        setupTable()
    }
    private fun loadExcludedProjects(): Set<String> {
        val settingsService = ApplicationManager.getApplication().getService(LANSettingsService::class.java)
        return settingsService.state.excludedProjects
    }

    private fun setupTable() {
        val columnNames = arrayOf("Project", "Exclude")
        val excludedProjects = loadExcludedProjects()
        val rowData = projects.map { arrayOf(it, it in excludedProjects) }.toTypedArray()

        tableModel = object : DefaultTableModel(rowData, columnNames) {
            override fun isCellEditable(row: Int, column: Int) = column == 1

            override fun getColumnClass(column: Int): Class<*> {
                return if (column == 1) java.lang.Boolean::class.java else super.getColumnClass(column)
            }
        }

        val table = JBTable(tableModel)
        val projectNameColumn = table.columnModel.getColumn(0)
        projectNameColumn.preferredWidth = (table.preferredSize.width * 0.8).toInt()

        val excludeColumn = table.columnModel.getColumn(1)
        excludeColumn.minWidth = 60
        excludeColumn.preferredWidth = 60
        excludeColumn.maxWidth = 80
        excludeColumn.cellRenderer = CustomBooleanTableCellRenderer()

        panel.add(JBScrollPane(table), BorderLayout.CENTER)
    }

    override fun getDisplayName() = "Last Action Note"
    override fun createComponent() = panel
    override fun isModified(): Boolean {
        val currentExcludedProjects = loadExcludedProjects()
        return (0 until tableModel.rowCount).any { rowIndex ->
            val projectName = tableModel.getValueAt(rowIndex, 0) as String
            val isExcluded = tableModel.getValueAt(rowIndex, 1) as Boolean
            isExcluded != (projectName in currentExcludedProjects)
        }
    }
    override fun apply() {
        val settingsService = ApplicationManager.getApplication().getService(LANSettingsService::class.java)
        settingsService.state.excludedProjects.clear()
        (0 until tableModel.rowCount).forEach { rowIndex ->
            val projectName = tableModel.getValueAt(rowIndex, 0) as String
            val isExcluded = tableModel.getValueAt(rowIndex, 1) as Boolean
            if (isExcluded) {
                settingsService.state.excludedProjects.add(projectName)
            }
        }
    }
    override fun reset() {
        val excludedProjects = loadExcludedProjects()
        (0 until tableModel.rowCount).forEach { rowIndex ->
            val projectName = tableModel.getValueAt(rowIndex, 0) as String
            tableModel.setValueAt(projectName in excludedProjects, rowIndex, 1)
        }
    }
}

class CustomBooleanTableCellRenderer : BooleanTableCellRenderer() {
    override fun getTableCellRendererComponent(
        table: JTable?,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        val component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
        if (component is JCheckBox) {
            component.horizontalAlignment = SwingConstants.CENTER
        }
        return component
    }
}
package com.mladich.lastactionnote.settings

import com.intellij.ide.RecentProjectListActionProvider
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.ui.BooleanTableCellRenderer
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.*
import javax.swing.table.DefaultTableModel

class SettingsPanel : SearchableConfigurable {
    private val panel = JPanel(BorderLayout())

    init {
        setupTable()
    }

    private fun setupTable() {
        val recentProjectsActions = RecentProjectListActionProvider.getInstance().getActions(false, false)
        val projects = recentProjectsActions.mapNotNull { (it as? AnAction)?.templatePresentation?.text }.toTypedArray()
        val columnNames = arrayOf("Project", "Exclude")
        val rowData = projects.map { arrayOf(it, false) }.toTypedArray()

        val tableModel = object : DefaultTableModel(rowData, columnNames) {
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

    override fun getId() = "LANToolsTab"
    override fun getDisplayName() = "Last Action Note"
    override fun createComponent() = panel
    override fun isModified() = false
    override fun apply() {}
    override fun reset() {}
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
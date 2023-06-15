package com.mladich.lastactionnote.tools

import com.intellij.openapi.components.Service
import com.intellij.openapi.vfs.VirtualFile

@Service
class FileHistory {
    private val maxSize = 5
    private val editedFiles: MutableList<String> = mutableListOf()
    private var fileCounter: Int= 0

    fun getCounter(): Int = fileCounter
    fun getMaxSize(): Int = maxSize

    fun increaseCounter() = fileCounter++
    fun getHistory(): List<String> {
        return editedFiles.toList()
    }

    fun addToHistory(file: VirtualFile) {
        editedFiles.remove(file.name) // Remove the file if it already exists in the list
        editedFiles.add(file.name) // Add the file to the beginning of the list
        if (editedFiles.size > maxSize) {
            editedFiles.removeLast() // Keep the list size limited to maxSize
        }
    }

    fun clearHistoryAndCounter() {
        editedFiles.clear()
        fileCounter = 0
    }
}
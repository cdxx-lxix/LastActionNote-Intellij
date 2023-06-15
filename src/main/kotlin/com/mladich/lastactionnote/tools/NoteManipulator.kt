package com.mladich.lastactionnote.tools

import com.google.gson.GsonBuilder
import com.intellij.AbstractBundle
import com.intellij.openapi.project.Project
import com.mladich.lastactionnote.dialogs.CloseNoteDialog
import com.mladich.lastactionnote.listeners.history
import com.mladich.lastactionnote.tools.CommonData.Companion.currentDate
import com.mladich.lastactionnote.tools.CommonData.Companion.myBundle
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class NoteManipulator {
    fun saveData(text: String?, files: List<String?>?, project: Project) {
        val note = Note(text, project.name, files)
        if (note.text?.isEmpty() == true) {
            note.text = AbstractBundle.message(myBundle, "empty.NoteText") // Sets the text only so it won't mess with files
        }
        val mrJson = File(project.basePath, ".lastactionnote")
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(note)
            try {
                FileWriter(mrJson).use { file -> file.write(jsonString) }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    fun openData(project: Project): Note {
        // Tries to read the .lastactionnote file if there is none sets the date to default values
        val mrJson = File(project.basePath, ".lastactionnote")
        var note: Note? = null
        try {
            note = if (mrJson.exists()) {
                readData(mrJson)
            } else {
                println("File does not exist")
                setEmptyNote(project)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (note != null) {
            println(note.files)
        }
        return note!!
    }

    private fun readData(data: File): Note? {
        // Reads the .lastactionnote file and returns it as a Note
        FileReader(data).use { fileReader ->
            val gson = GsonBuilder().setPrettyPrinting().create()
            return gson.fromJson(fileReader, Note::class.java)
        }
    }

    fun setEmptyNote(project: Project): Note {
        // Sets Note's values to default
        val testList: MutableList<String> = ArrayList()
        testList.add(AbstractBundle.message(myBundle, "empty.NoteFiles"))
        return Note(AbstractBundle.message(myBundle, "empty.NoteText"), project.name, testList)
    }

    class Note internal constructor(var text: String?, var projectName: String, var files: List<String?>?, var date: String = currentDate, var fileCounter: Int = history.getCounter())

}



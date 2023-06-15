package com.mladich.lastactionnote.tools

import com.google.gson.GsonBuilder
import com.intellij.AbstractBundle
import com.intellij.openapi.project.Project
import com.mladich.lastactionnote.dialogs.CloseNoteDialog
import com.mladich.lastactionnote.tools.CommonData.Companion.myBundle
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class NoteManipulator {
    fun saveData(text: String?, files: List<String?>?, project: Project) {
        val note = Note(text, files)
        val mrJson = File(project.basePath, ".lastactionnote")
        writeData(note, mrJson)
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
                setEmptyNote()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (note != null) {
            println(note.files)
        }
        return note!!
    }

    private fun writeData(data: Note, dataFile: File) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(data)
        println(jsonString)
        if (data.text?.isEmpty() == true) {
            setEmptyNote()
        } else {
            try {
                FileWriter(dataFile).use { file -> file.write(jsonString) }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun readData(data: File): Note? {
        // Reads the .lastactionnote file and returns it as a Note
        FileReader(data).use { fileReader ->
            val gson = GsonBuilder().setPrettyPrinting().create()
            return gson.fromJson(fileReader, Note::class.java)
        }
    }

    private fun setEmptyNote(): Note {
        // Sets Note's values to default
        val testList: MutableList<String> = ArrayList()
        testList.add(AbstractBundle.message(myBundle, "empty.NoteFiles"))
        return Note(AbstractBundle.message(myBundle, "empty.NoteText"), testList)
    }

    class Note internal constructor(var text: String?, var files: List<String?>?)

}


package controllers

import models.Note
import persistence.Serializer
import persistence.XMLSerializer
import java.io.File

class NoteAPI(serializerType: Serializer) {

    private var serializer: Serializer = serializerType


    private var notes = ArrayList<Note>()

    fun add(note: Note): Boolean {
        return notes.add(note)
    }

    fun listAllNotes(): String =
        if  (notes.isEmpty()) "No notes stored"
        else notes.joinToString (separator = "\n") { note ->
            notes.indexOf(note).toString() + ": " + note.toString() }



    fun archiveNote(indexToArchive: Int): Boolean {
        if (isValidIndex(indexToArchive)) {
            val noteToArchive = notes[indexToArchive]
            if (!noteToArchive.isNoteArchived) {
                noteToArchive.isNoteArchived = true
                return true
            }
        }
        return false
    }

    fun numberOfNotes(): Int {
        return notes.size
    }

    fun findNote(index: Int): Note? {
        return if (isValidListIndex(index, notes)) {
            notes[index]
        } else null
    }

    //utility method to determine if an index is valid in a list.
    fun isValidListIndex(index: Int, list: List<Any>): Boolean {
        return (index >= 0 && index < list.size)
    }
    fun deleteNote(indexToDelete: Int): Note? {
        return if (isValidListIndex(indexToDelete, notes)) {
            notes.removeAt(indexToDelete)
        } else null
    }


    fun listNotesBySelectedPriority(priority: Int): String =
        if (notes.isEmpty()) {
            "No notes stored"
        } else {
            val notesWithPriority = notes.filter { it.notePriority == priority }
            if (notesWithPriority.isEmpty()) {
                "No notes with priority: $priority"
            } else {
                "${numberOfNotesByPriority(priority)} notes with priority $priority:\n" +
                        notesWithPriority.joinToString("\n") { "${notes.indexOf(it)}: $it" }
            }
        }


    /* fun numberOfNotesByPriority(): Int {
        return notes.stream()
            .filter{note: Note -> note.notePriority}
            .count()
            .toInt()

    }*/
    fun numberOfNotesByPriority(priority: Int): Int {
        return notes.count { note -> note.notePriority == priority }
    }



    fun updateNote(indexToUpdate: Int, note: Note?): Boolean {
        //find the note object by the index number
        val foundNote = findNote(indexToUpdate)

        //if the note exists, use the note details passed as parameters to update the found note in the ArrayList.
        if ((foundNote != null) && (note != null)) {
            foundNote.noteTitle = note.noteTitle
            foundNote.notePriority = note.notePriority
            foundNote.noteCategory = note.noteCategory
            return true
        }

        //if the note was not found, return false, indicating that the update was not successful
        return false
    }

    fun isValidIndex(index: Int): Boolean {
        return isValidListIndex(index, notes);
    }

    @Throws(Exception::class)
    fun load() {
        notes = serializer.read() as ArrayList<Note>
    }

    @Throws(Exception::class)
    fun store() {
        serializer.write(notes)
    }

    fun listActiveNotes(): String =
        if (numberOfActiveNotes() == 0) {
            "No active notes stored"
        } else {
            notes.filterNot { it.isNoteArchived }
                .joinToString(separator = "\n") { note -> "${notes.indexOf(note)}: $note" }
        }


    fun listArchivedNotes(): String =
        if (numberOfArchivedNotes() == 0) "No archived notes stored"
        else notes.filter { it.isNoteArchived }
            .joinToString(separator = "\n") { note -> "${notes.indexOf(note)}: $note" }



    fun numberOfArchivedNotes(): Int {
        return notes.stream()
            .filter{note: Note -> note.isNoteArchived}
            .count()
            .toInt()
    }




    fun numberOfActiveNotes(): Int {
        return notes.stream()
            .filter{note: Note -> !note.isNoteArchived}
            .count()
            .toInt()
    }


}

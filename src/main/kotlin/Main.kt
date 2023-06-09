import ScannerInput.readNextInt
import ScannerInput.readNextLine
import controllers.NoteAPI
import models.Note
import java.lang.System.exit

import mu.KotlinLogging
import persistence.JSONSerializer
import persistence.Serializer
import persistence.XMLSerializer
import java.io.File


private val logger = KotlinLogging.logger {}

private val noteAPI = NoteAPI(XMLSerializer(File("notes.xml")))
//private val noteAPI = NoteAPI(JSONSerializer(File("notes.json")))


fun main(args: Array<String>) {
    runMenu()
}

fun runMenu() {
    do {
        val option = mainMenu()
        when (option) {
            1 -> ModifyNote()
            2 -> listNotes()
            3 -> archiveNote()
            4 -> searchNotes()
            20 -> save()
            21 -> load()
            0 -> exitApp()
            else -> System.out.println("invalid option entered: ${option}")
        }
    } while (true)
}

fun mainMenu(): Int {
    return readNextInt(
        """ 
         > ----------------------------------
         > |        NOTE KEEPER APP         |
         > ----------------------------------
         > | NOTE MENU                      |
         > |   1) Modify Notes              |
         > |   2) List all notes            |
         > |   3) Archive a note            |
         > |   4) Search Notes By Title     |
         > ----------------------------------
         > |   20) Save notes               |
         > |   21) Load notes               |
         > |   0) Exit                      |
         > ----------------------------------
         > ==>> """.trimMargin(">")
    )
}

fun ModifyNote(){
        val option = readNextInt(
            """
                  > --------------------------------
                  > |   1) Add Note                |
                  > |   2) Delete Note             |
                  > |   3) Update Existing Note    |
                  > |   4) Delete Completed Notes  |
                  > --------------------------------
         > ==>> """.trimMargin(">"))

        when (option) {
            1 -> addNote()
            2 -> deleteNote()
            3 -> updateNote()
            4 -> deleteCompletedNotes()

            else -> println("Invalid option entered: " + option);
        }
    }


fun addNote(){
    //logger.info { "addNote() function invoked" }
    val noteTitle = readNextLine("Enter a title for the note: ")
    val notePriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
    val noteCategory = readNextLine("Enter a category for the note: ")
    val author = readNextLine("PLease Enter your Name: ")
    val isAdded = noteAPI.add(Note(noteTitle, notePriority, noteCategory, false, author, false))

    if (isAdded) {
        println("Added Successfully")
    } else {
        println("Add Failed")
    }
}
fun searchNotes() {
    val searchTitle = readNextLine("Enter the Title to search by: ")
    val searchResults = noteAPI.searchByTitle(searchTitle)
    if (searchResults.isEmpty()) {
        println("No notes found")
    } else {
        println(searchResults)
    }
}

fun listNotes() {
    if (noteAPI.numberOfNotes() > 0) {
        val option = readNextInt(
            """
                  > --------------------------------
                  > |   1) View ALL notes          |
                  > |   2) View ACTIVE notes       |
                  > |   3) View ARCHIVED notes     |
                  > --------------------------------
         > ==>> """.trimMargin(">"))

        when (option) {
            1 -> listAllNotes();
            2 -> listActiveNotes();
            3 -> listArchivedNotes();
            else -> println("Invalid option entered: " + option);
        }
    } else {
        println("Option Invalid - No notes stored");
    }
}
fun updateNote() {
    listNotes()

    if (noteAPI.numberOfNotes() > 0) {
        val indexToUpdate = readNextInt("Enter the index of the note to update: ")

        if (noteAPI.isValidIndex(indexToUpdate)) {
            val noteTitle = readNextLine("Enter a title for the note: ")
            val notePriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
            val noteCategory = readNextLine("Enter a category for the note: ")
            val author = readNextLine("Please enter your Name: ")
            var isCompleted = readNextLine("Is this Note Completed?: (yes/no)").equals("yes",ignoreCase = true)

            if (noteAPI.updateNote(indexToUpdate, Note(noteTitle, notePriority, noteCategory, false, author, isCompleted))) {
                println("Update Successful")
            } else {
                println("Update Failed")
            }
        } else {
            println("Invalid index entered")
        }
    } else {
        println("No notes stored")
    }
}


fun deleteCompletedNotes() {
    val isDeleted = noteAPI.deleteCompletedNotes()

    if (isDeleted) {
        println("Completed notes deleted successfully")
    } else {
        println("No completed notes to delete")
    }
}


fun deleteNote(){
    // logger.info { "deleteNote() function invoked" }
    listNotes()
    if (noteAPI.numberOfNotes() > 0) {
        //only ask the user to choose the note to delete if notes exist
        val indexToDelete = readNextInt("Enter the index of the note to delete: ")
        //pass the index of the note to NoteAPI for deleting and check for success.
        val noteToDelete = noteAPI.deleteNote(indexToDelete)
        if (noteToDelete != null) {
            println("Delete Successful! Deleted note: ${noteToDelete.noteTitle}")
        } else {
            println("Delete NOT Successful")
        }
    }
}
fun save() {
    try {
        noteAPI.store()
    } catch (e: Exception) {
        System.err.println("Error writing to file: $e")
    }
}

fun load() {
    try {
        noteAPI.load()
    } catch (e: Exception) {
        System.err.println("Error reading from file: $e")
    }
}

fun listActiveNotes() {
    println(noteAPI.listActiveNotes())
}

fun archiveNote() {
    listActiveNotes()
    if (noteAPI.numberOfActiveNotes() > 0) {
        //only ask the user to choose the note to archive if active notes exist
        val indexToArchive = readNextInt("Enter the index of the note to archive: ")
        //pass the index of the note to NoteAPI for archiving and check for success.
        if (noteAPI.archiveNote(indexToArchive)) {
            println("Archive Successful!")
        } else {
            println("Archive NOT Successful")
        }
    }
}

fun listAllNotes() {
    println(noteAPI.listAllNotes())
}

fun listArchivedNotes() {
    println(noteAPI.listArchivedNotes())
}




fun exitApp(){
    println("Exiting...bye")
    exit(0)
}

package models
import kotlinx.serialization.Serializable

@Serializable
data class Note(var noteTitle: String, var notePriority: Int, var noteCategory: String, var isNoteArchived :Boolean){
}





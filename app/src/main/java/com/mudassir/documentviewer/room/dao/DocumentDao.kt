package com.mudassir.documentviewer.room.dao

import androidx.room.*
import com.mudassir.documentviewer.room.entity.DocNoteModel
import com.mudassir.documentviewer.room.entity.DocNoteModelForRecent

@Dao
interface DocumentDao {
    @Query("SELECT * FROM document_view_entity WHERE is_trash == 0 ORDER BY id DESC")
    fun getAllNotes(): List<DocNoteModel>

    @Query("UPDATE document_view_entity SET is_trash = :isTrash WHERE id = :notesID")
    fun updateForTrash(isTrash: Boolean, notesID: Int)

    @Query("DELETE FROM document_view_entity WHERE id = :notesID")
    fun deleteBasedOnID(notesID: Int)

    @Query("SELECT * FROM document_view_entity WHERE is_trash == 1 ORDER BY id DESC")
    fun getAllTrashNotes(): List<DocNoteModel>

    @Query("UPDATE document_view_entity SET is_favorite = :isFav WHERE id = :notesID")
    fun updateForFavorite(isFav: Boolean, notesID: Int)

    @Query("SELECT EXISTS(SELECT * FROM document_view_entity WHERE title = :title)")
    fun checkISDocExistInFav(title: String): Boolean

    @Query("SELECT * FROM document_view_entity WHERE is_favorite == 1 ORDER BY id DESC")
    fun getAllFavoriteNotes(): List<DocNoteModel>

    // Sorting
    @Query("SELECT * FROM document_view_entity WHERE is_trash == 0 ORDER BY id ASC")
    fun getAllSortByOldest(): List<DocNoteModel>

    @Query("SELECT * FROM document_view_entity WHERE is_trash == 0 ORDER BY title ASC")
    fun getAllSortByAZ(): List<DocNoteModel>

    @Query("SELECT * FROM document_view_entity WHERE is_trash == 0 ORDER BY title DESC")
    fun getAllSortByZA(): List<DocNoteModel>
    // Sorting


    @Delete
    fun deleteNote(note: DocNoteModel)

    @Query("UPDATE document_view_entity SET is_trash = :isTrash WHERE id in (:idList)")
    fun updateForTrashMultiple(isTrash: Boolean, idList: List<Int>)

    @Query("DELETE FROM document_view_entity WHERE id in (:idList)")
    fun deleteMultipleNotes(idList: List<Int>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: List<DocNoteModel>)

    //For recent

    @Query("UPDATE document_view_entity SET isRecent = :isRecent WHERE id = :notesID")
    fun updateForRecent(isRecent: Boolean, notesID: Int)

    @Query("SELECT * FROM document_view_entity WHERE isRecent == 1 ORDER BY id DESC")
    fun getAllRecentNotes(): List<DocNoteModel>
    // recent end

    @Query("SELECT * FROM document_view_entity")
    fun getAllDocs(): List<DocNoteModel>
}
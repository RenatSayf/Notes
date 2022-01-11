package com.notes.data

import androidx.room.*

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes")
    fun getAll(): List<NoteDbo>

    @Query("SELECT * FROM notes ORDER BY CASE WHEN :isAsc == 1 THEN modifiedAt END ASC, CASE WHEN :isAsc == 0 THEN modifiedAt END DESC")
    fun getAllSortedByModifiedTime(isAsc: Boolean): List<NoteDbo>

    @Query("SELECT * FROM notes WHERE id == :id")
    fun get(id: Long): NoteDbo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg notes: NoteDbo)

    @Update(entity = NoteDbo::class, onConflict = OnConflictStrategy.REPLACE)
    fun update(note: NoteDbo): Int

    @Query("DELETE FROM notes WHERE id == :id")
    fun delete(vararg id: Long): Int

}
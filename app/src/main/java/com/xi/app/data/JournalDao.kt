package com.xi.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY id DESC")
    fun getAllEntries(): Flow<List<JournalEntry>>

    @Insert
    suspend fun insert(entry: JournalEntry): Long

    @Query("SELECT COUNT(DISTINCT date) FROM journal_entries")
    suspend fun getStreakCount(): Int
}

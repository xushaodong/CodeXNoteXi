package com.xi.app.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class JournalRepository private constructor(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val journalDao = database.journalDao()
    
    private val _entries = MutableStateFlow<List<JournalEntry>>(emptyList())
    val entries: StateFlow<List<JournalEntry>> = _entries.asStateFlow()

    init {
        // 观察数据库变化并更新内存状态
        CoroutineScope(Dispatchers.IO).launch {
            journalDao.getAllEntries().collect {
                _entries.value = it
            }
        }
    }

    suspend fun record(prompt: String, content: String): JournalEntry {
        Log.d("JournalRepository", "Recording new entry to Database.")
        
        val quote = content.split("。", "！", "？", "\n").firstOrNull { it.isNotBlank() }
            ?.trim()
            ?.take(32)
            ?: "在裂缝中，寻找光。"

        val entry = JournalEntry(
            date = LocalDate.now().toString(),
            prompt = prompt,
            content = content,
            quote = quote
        )
        
        val id = journalDao.insert(entry)
        Log.d("JournalRepository", "Entry recorded to DB with id: $id")
        
        return entry.copy(id = id)
    }

    suspend fun streakDays(): Int {
        return journalDao.getStreakCount()
    }

    companion object {
        @Volatile
        private var INSTANCE: JournalRepository? = null

        fun getInstance(context: Context): JournalRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = JournalRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}

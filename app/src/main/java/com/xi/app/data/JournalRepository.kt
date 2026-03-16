package com.xi.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicLong

object JournalRepository {
    private val idGenerator = AtomicLong(0)
    private val entries = MutableStateFlow<List<JournalEntry>>(emptyList())

    fun observeEntries(): StateFlow<List<JournalEntry>> = entries.asStateFlow()

    fun record(prompt: String, content: String): JournalEntry {
        val quote = content.split("。", "！", "？", "\n").firstOrNull { it.isNotBlank() }
            ?.trim()
            ?.take(32)
            ?: "在裂缝中，寻找光。"

        val entry = JournalEntry(
            id = idGenerator.incrementAndGet(),
            date = LocalDate.now().toString(),
            prompt = prompt,
            content = content,
            quote = quote
        )
        entries.value = listOf(entry) + entries.value
        return entry
    }

    fun streakDays(): Int {
        return entries.value.map { it.date }.distinct().size
    }
}

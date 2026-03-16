package com.xi.app.data

data class JournalEntry(
    val id: Long,
    val date: String,
    val prompt: String,
    val content: String,
    val quote: String
)

package com.xi.app.ui.snapshot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xi.app.data.JournalRepository
import com.xi.app.databinding.FragmentSnapshotResultBinding

class SnapshotResultActivity : AppCompatActivity() {
    private lateinit var binding: FragmentSnapshotResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSnapshotResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val entryId = intent.getLongExtra(EXTRA_ENTRY_ID, -1)
        val entry = JournalRepository.observeEntries().value.firstOrNull { it.id == entryId }

        binding.quoteText.text = "“${entry?.quote ?: "在裂缝中，寻找光。"}”"
        binding.dateCaption.text = entry?.date ?: "-"
    }

    companion object {
        const val EXTRA_ENTRY_ID = "extra_entry_id"
    }
}

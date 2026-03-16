package com.xi.app.ui.snapshot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xi.app.data.JournalRepository
import com.xi.app.databinding.FragmentSnapshotResultBinding

class SnapshotResultActivity : AppCompatActivity() {
    private lateinit var binding: FragmentSnapshotResultBinding
    private lateinit var repository: JournalRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSnapshotResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = JournalRepository.getInstance(this)

        val entryId = intent.getLongExtra(EXTRA_ENTRY_ID, -1)
        // 查找对应的笔记
        val entry = repository.entries.value.firstOrNull { it.id == entryId }

        // 填充数据
        binding.dateCaption.text = entry?.date?.replace("-", ".") ?: "2024.03.16"
        binding.quoteText.text = "“${entry?.quote ?: "在裂缝中，寻找光。"}”"
        binding.fullContentText.text = entry?.content ?: ""

        // 点击关闭按钮返回
        binding.closeButton.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val EXTRA_ENTRY_ID = "extra_entry_id"
    }
}

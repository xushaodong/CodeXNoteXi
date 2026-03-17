package com.xi.app.ui.writing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.xi.app.R
import com.xi.app.data.JournalRepository
import com.xi.app.databinding.ActivityMainBinding
import com.xi.app.databinding.ActivityZenWritingBinding
import com.xi.app.ui.snapshot.SnapshotResultActivity
import kotlinx.coroutines.launch

class ZenWritingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityZenWritingBinding
    private lateinit var repository: JournalRepository
    private var originalContent: String? = null
    private var currentPrompt: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. 开启全面屏适配 (Edge-to-Edge)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityZenWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = JournalRepository.getInstance(this)

        currentPrompt = intent.getStringExtra(EXTRA_PROMPT).orEmpty()
        originalContent = intent.getStringExtra(EXTRA_CONTENT)

        binding.promptIndicator.text = currentPrompt

        if (originalContent != null) {
            binding.writingInput.setText(originalContent)
            binding.writingInput.setSelection(originalContent?.length ?: 0)
            binding.moreButton.visibility = View.VISIBLE
        } else {
            binding.moreButton.visibility = View.GONE
        }

        binding.backButton.setOnClickListener { handleExit() }
        binding.moreButton.setOnClickListener { showMoreMenu(it) }

        binding.writingRoot.setOnClickListener {
            binding.writingInput.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.writingInput, InputMethodManager.SHOW_IMPLICIT)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = handleExit()
        })

        binding.writingInput.doAfterTextChanged { text ->
            binding.wordHintText.visibility =
                if ((text?.length ?: 0) >= 300) View.VISIBLE else View.GONE
        }

        binding.freezeButton.setOnClickListener { saveNote() }
    }

    private fun showMoreMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_zen_more, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_share -> {
                    android.widget.Toast.makeText(
                        this, "分享功能正在打磨中...", android.widget.Toast.LENGTH_SHORT
                    ).show()
                    true
                }
                R.id.action_delete -> {
                    confirmDelete()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this, R.style.Xi_AlertDialog)
            .setTitle("焚毁记忆")
            .setMessage("这篇文字将永远消失在雨夜中，确认焚毁吗？")
            .setNegativeButton("留住它", null)
            .setPositiveButton("确认焚毁") { _, _ ->
                lifecycleScope.launch {
                    repository.delete(currentPrompt, originalContent ?: "")
                    android.widget.Toast.makeText(
                        this@ZenWritingActivity, "已焚毁", android.widget.Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
            .show()
    }

    private fun saveNote() {
        lifecycleScope.launch {
            try {
                vibrateHeavy()
                val content =
                    binding.writingInput.text?.toString().orEmpty().ifBlank { "今天我先从沉默开始。" }
                val entry = repository.record(currentPrompt, content)
                val intent =
                    Intent(this@ZenWritingActivity, SnapshotResultActivity::class.java).apply {
                        putExtra(SnapshotResultActivity.EXTRA_ENTRY_ID, entry.id)
                    }
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Log.e("ZenWritingActivity", "Error during saving: ${e.message}", e)
            }
        }
    }

    private fun handleExit() {
        val currentContent = binding.writingInput.text?.toString().orEmpty()
        val isNotModified = originalContent != null && currentContent == originalContent
        val isEmptyNew = originalContent == null && currentContent.isBlank()
        if (isNotModified || isEmptyNew) finish() else confirmExit()
    }

    private fun confirmExit() {
        AlertDialog.Builder(this, R.style.Xi_AlertDialog)
            .setMessage("确认离开？你的草稿会自动保存。")
            .setNegativeButton("继续书写", null)
            .setPositiveButton("离开") { _, _ -> finish() }
            .show()
    }

    private fun vibrateHeavy() {
        try {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(
                VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } catch (e: Exception) {
            Log.w("ZenWritingActivity", "Vibration failed: ${e.message}")
        }
    }

    companion object {
        const val EXTRA_PROMPT = "extra_prompt"
        const val EXTRA_CONTENT = "extra_content"
    }
}
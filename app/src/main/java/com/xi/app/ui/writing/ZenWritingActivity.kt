package com.xi.app.ui.writing

import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.xi.app.data.JournalRepository
import com.xi.app.databinding.ActivityZenWritingBinding
import com.xi.app.ui.snapshot.SnapshotResultActivity
import kotlinx.coroutines.launch

class ZenWritingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityZenWritingBinding
    private lateinit var repository: JournalRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityZenWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = JournalRepository.getInstance(this)

        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        hideSystemUi()

        val prompt = intent.getStringExtra(EXTRA_PROMPT).orEmpty()
        val existingContent = intent.getStringExtra(EXTRA_CONTENT)
        
        Log.d("ZenWritingActivity", "Activity created. Prompt: $prompt, hasExistingContent: ${existingContent != null}")

        if (existingContent != null) {
            binding.writingInput.setText(existingContent)
            binding.writingInput.setSelection(existingContent.length)
        }

        binding.backButton.setOnClickListener { confirmExit() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = confirmExit()
        })

        binding.writingInput.doAfterTextChanged { text ->
            binding.wordHintText.visibility = if ((text?.length ?: 0) >= 300) android.view.View.VISIBLE else android.view.View.GONE
        }

        binding.freezeButton.setOnClickListener {
            Log.d("ZenWritingActivity", "Freeze button clicked")
            lifecycleScope.launch {
                try {
                    vibrateHeavy()
                    val content = binding.writingInput.text?.toString().orEmpty().ifBlank { "今天我先从沉默开始。" }
                    
                    val entry = repository.record(prompt, content)
                    
                    val intent = Intent(this@ZenWritingActivity, SnapshotResultActivity::class.java).apply {
                        putExtra(SnapshotResultActivity.EXTRA_ENTRY_ID, entry.id)
                    }
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    Log.e("ZenWritingActivity", "Error during saving: ${e.message}", e)
                }
            }
        }
    }

    private fun confirmExit() {
        AlertDialog.Builder(this)
            .setMessage("确认离开？你的草稿会自动保存。")
            .setNegativeButton("继续书写", null)
            .setPositiveButton("离开") { _, _ -> finish() }
            .show()
    }

    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun vibrateHeavy() {
        try {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } catch (e: Exception) {
            Log.w("ZenWritingActivity", "Vibration failed: ${e.message}")
        }
    }

    companion object {
        const val EXTRA_PROMPT = "extra_prompt"
        const val EXTRA_CONTENT = "extra_content"
    }
}

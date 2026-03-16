package com.xi.app.ui.writing

import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.doAfterTextChanged
import com.xi.app.data.JournalRepository
import com.xi.app.databinding.ActivityZenWritingBinding
import com.xi.app.ui.snapshot.SnapshotResultActivity

class ZenWritingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityZenWritingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityZenWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        hideSystemUi()

        val prompt = intent.getStringExtra(EXTRA_PROMPT).orEmpty()

        binding.backButton.setOnClickListener { confirmExit() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = confirmExit()
        })

        binding.writingInput.doAfterTextChanged { text ->
            binding.wordHintText.visibility = if ((text?.length ?: 0) >= 300) android.view.View.VISIBLE else android.view.View.GONE
        }

        binding.freezeButton.setOnClickListener {
            vibrateHeavy()
            val content = binding.writingInput.text?.toString().orEmpty().ifBlank { "今天我先从沉默开始。" }
            val entry = JournalRepository.record(prompt, content)
            startActivity(Intent(this, SnapshotResultActivity::class.java).apply {
                putExtra(SnapshotResultActivity.EXTRA_ENTRY_ID, entry.id)
            })
            finish()
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
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    companion object {
        const val EXTRA_PROMPT = "extra_prompt"
    }
}

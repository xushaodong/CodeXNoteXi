package com.xi.app.ui.view

import android.R.attr.height
import android.R.attr.width
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View

class RainyGlassView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    data class RainDrop(
        var x: Float, var y: Float,
        var speed: Float, var length: Float, var alpha: Float
    )

    private val drops = mutableListOf<RainDrop>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeCap = Paint.Cap.ROUND
    }
    private val bgPaint = Paint().apply {
        shader = LinearGradient(
            0f, 0f, 0f, 1f,
            intArrayOf(0xFF0F172A.toInt(), 0xFF1E293B.toInt(), 0xFF020617.toInt()),
            null, Shader.TileMode.CLAMP
        )
    }

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            updateDrops()
            invalidate()
            handler.postDelayed(this, 16) // ~60fps
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        bgPaint.shader = LinearGradient(
            0f, 0f, 0f, h.toFloat(),
            intArrayOf(0xFF0F172A.toInt(), 0xFF1E293B.toInt(), 0xFF020617.toInt()),
            null, Shader.TileMode.CLAMP
        )
        // 初始化雨滴
        repeat(80) { drops.add(randomDrop(w, h, true)) }
    }

    private fun randomDrop(w: Int, h: Int, randomY: Boolean = false) = RainDrop(
        x = (0..w).random().toFloat(),
        y = if (randomY) (0..h).random().toFloat() else -20f,
        speed = (8..20).random().toFloat(),
        length = (20..60).random().toFloat(),
        alpha = (30..90).random() / 100f
    )

    private fun updateDrops() {
        val h = height
        val w = width
        drops.replaceAll { drop ->
            drop.y += drop.speed
            if (drop.y > h + drop.length) randomDrop(w, h) else drop
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        drops.forEach { drop ->
            paint.alpha = (drop.alpha * 255).toInt()
            paint.strokeWidth = 1.5f
            canvas.drawLine(drop.x, drop.y, drop.x - 2f, drop.y + drop.length, paint)
        }
    }

    override fun onAttachedToWindow() { super.onAttachedToWindow(); handler.post(runnable) }
    override fun onDetachedFromWindow() { super.onDetachedFromWindow(); handler.removeCallbacks(runnable) }
}
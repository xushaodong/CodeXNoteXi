package com.xi.app.core

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PromptEngine {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    private val onboardingPrompts = listOf(
        "今天，什么事情让你感到了片刻的安静？",
        "你今天最想感谢的一个瞬间是什么？",
        "此刻你最真实的感受，是什么颜色？"
    )

    private val reflectivePrompts = listOf(
        "你上一次真正改变某个根深蒂固的想法，是什么时候？",
        "如果你的恐惧可以说话，它现在想对你说什么？",
        "最近你在回避的那件事，是否也在等待你温柔地看见它？"
    )

    fun todayDate(): String = LocalDate.now().format(dateFormatter)

    fun promptFor(streakDays: Int): String {
        return when {
            streakDays < 7 -> onboardingPrompts[streakDays % onboardingPrompts.size]
            else -> reflectivePrompts[LocalDate.now().dayOfYear % reflectivePrompts.size]
        }
    }
}

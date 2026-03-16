package com.xi.app.core

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class PromptEngine {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    private val zenPrompts = listOf(
        "若此刻是生命中最后一个黄昏，你会写下什么？",
        "剥离掉所有的身份标签，剩下的那个你，是谁？",
        "在那些不被察觉的裂缝里，你是否看见了光？",
        "如果时间是静止的，你最想停留在哪一个瞬间？",
        "此时此刻，你内心的潮汐正往哪个方向流动？",
        "那些未曾说出口的话，最终变成了你身体的哪一部分？",
        "如果你是一棵树，这季的落叶里藏着怎样的告白？",
        "在喧嚣的尽头，你听见了哪一种沉默？",
        "生活给你的那些磨损，是否也让你变得更加透明？",
        "如果灵魂有形状，它今天被什么事物修剪过？"
    )

    fun todayDate(): String = LocalDate.now().format(dateFormatter)

    /**
     * 随机获取一句引导词
     * 满足用户“每次打开或来到主页都随机显示”的需求
     */
    fun getRandomPrompt(): String {
        return zenPrompts[Random.nextInt(zenPrompts.size)]
    }

    // 保留原有接口以防其他地方调用，但逻辑改为随机
    fun promptFor(streakDays: Int): String {
        return getRandomPrompt()
    }
}

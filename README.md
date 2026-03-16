# 隙 Xi (Android Native)

基于 PRD V1.0 的 Android 原生实现骨架（Kotlin + XML + MVVM/Clean 可扩展结构）。

## 已实现（Alpha 可运行范围）

- 今日提问页（当日问题 + 日期 + 点击进入书写）
- 禅意书写页（全屏沉浸、FLAG_SECURE、防误触返回、300字微提示、定格按钮震动）
- 定格结果页（展示金句与日期，作为 AI 海报流程占位）
- 画廊页（基础历史列表）
- 个人页与设置页入口
- 极简三 Tab 导航
- PRD 品牌色与基础视觉符号（隙 logo）

## 技术说明

- `minSdk=26`，`targetSdk=34`
- Kotlin + ViewBinding
- 简化数据层：`JournalRepository`（当前为内存实现，后续可替换 Room + SQLCipher）
- 提问策略：`PromptEngine`（入门 + 反思两段策略）

## 后续建议（对齐 PRD V1.0 完整版）

1. 接入 Room + SQLCipher + EncryptedSharedPreferences
2. BiometricPrompt 解锁流程
3. 音效引擎 / Haptic 强度配置
4. LLM 情绪分析与图像生成 API
5. 画廊多视图、订阅限制与 Google Play Billing
6. Firebase Analytics / Crashlytics / FCM


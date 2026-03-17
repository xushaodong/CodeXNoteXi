package com.xi.app.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.xi.app.R
import com.xi.app.databinding.ActivityMainBinding
import com.xi.app.ui.gallery.GalleryFragment
import com.xi.app.ui.profile.ProfileFragment
import com.xi.app.ui.today.TodayPromptFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    
    // 使用 Map 存储 Fragment 实例，实现复用以保持状态
    private val fragments = mutableMapOf<Int, Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. 开启全面屏适配 (Edge-to-Edge)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. 处理窗口缩进 (Window Insets)，确保底部导航栏不被手势栏遮挡
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNav) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            // 初始化首页并显示
            showFragment(R.id.nav_today)
        }

        binding.bottomNav.setOnItemSelectedListener {
            showFragment(it.itemId)
            true
        }
    }

    /**
     * 使用 add/hide/show 模式管理 Fragment
     * 相比 replace，这种方式能完美保留 Fragment 内部的 View 状态（如滚动位置）
     */
    private fun showFragment(navId: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        
        // 1. 隐藏当前所有已存在的 Fragment
        fragments.values.forEach { transaction.hide(it) }
        
        // 2. 获取或创建目标 Fragment
        var fragment = fragments[navId]
        if (fragment == null) {
            fragment = when (navId) {
                R.id.nav_today -> TodayPromptFragment()
                R.id.nav_gallery -> GalleryFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> throw IllegalArgumentException("Unknown navId")
            }
            fragments[navId] = fragment
            transaction.add(R.id.container, fragment)
        } else {
            // 如果已存在，直接显示
            transaction.show(fragment)
        }
        
        transaction.commit()
    }
}

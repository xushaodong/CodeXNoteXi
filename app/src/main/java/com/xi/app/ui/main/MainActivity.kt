package com.xi.app.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.xi.app.R
import com.xi.app.databinding.ActivityMainBinding
import com.xi.app.ui.gallery.GalleryFragment
import com.xi.app.ui.profile.ProfileFragment
import com.xi.app.ui.today.TodayPromptFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            switchFragment(TodayPromptFragment())
        }

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_today -> switchFragment(TodayPromptFragment())
                R.id.nav_gallery -> switchFragment(GalleryFragment())
                R.id.nav_profile -> switchFragment(ProfileFragment())
            }
            true
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}

package com.example.friendlykeyboard

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.friendlykeyboard.databinding.ActivityMainBinding
import com.example.friendlykeyboard.fragments.HomeFragment
import com.example.friendlykeyboard.fragments.NotificationFragment
import com.example.friendlykeyboard.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var homeFragment: HomeFragment
    private lateinit var notificationFragment: NotificationFragment
    private lateinit var settingsFragment: SettingsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val settingHeader = findViewById<ConstraintLayout>(R.id.setting_header)
        val submitButton = settingHeader.findViewById<TextView>(R.id.submit_text)
        submitButton.visibility = View.GONE

        initFragments()
        initClickListener()
    }

    private fun initFragments() {
        homeFragment = HomeFragment()
        notificationFragment = NotificationFragment()
        settingsFragment = SettingsFragment()
        changeFragment(homeFragment)
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, fragment)
            commit()
        }
    }

    private fun initClickListener() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.tab_home -> {
                    // TODO: headerTitle 을 나중에 수정할 것!
                    changeFragment(homeFragment)
                    runOnUiThread {
                        binding.settingHeader.headerTitle.text = "키보드 설정"
                    }
                }
                R.id.tab_notification -> {
                    changeFragment(notificationFragment)
                    runOnUiThread {
                        binding.settingHeader.headerTitle.text = "알림"
                    }
                }
                R.id.tab_settings -> {
                    changeFragment(settingsFragment)
                    runOnUiThread {
                        binding.settingHeader.headerTitle.text = "설정"
                    }
                }
            }
            true
        }
    }
}
package com.example.friendlykeyboard.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.friendlykeyboard.R
import com.example.friendlykeyboard.databinding.ActivityMainBinding
import com.example.friendlykeyboard.fragments.HomeFragment
import com.example.friendlykeyboard.fragments.ChartFragment
import com.example.friendlykeyboard.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var homeFragment: HomeFragment
    private lateinit var chartFragment: ChartFragment
    private lateinit var settingsFragment: SettingsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        initFragments()
        initClickListener()
    }

    private fun initFragments() {
        homeFragment = HomeFragment()
        chartFragment = ChartFragment()
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
                    changeFragment(homeFragment)
                    runOnUiThread {
                        supportActionBar?.title = "홈"
                    }
                }
                R.id.tab_chart -> {
                    changeFragment(chartFragment)
                    runOnUiThread {
                        supportActionBar?.title = "통계"
                    }
                }
                R.id.tab_settings -> {
                    changeFragment(settingsFragment)
                    runOnUiThread {
                        supportActionBar?.title = "설정"
                    }
                }
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.appbar_action, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_logout -> {
            val editor = getSharedPreferences("cbAuto", 0).edit()
            editor.putBoolean("check", false)
            editor.putString("id", "")
            editor.putString("pwd", "")
            editor.apply()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
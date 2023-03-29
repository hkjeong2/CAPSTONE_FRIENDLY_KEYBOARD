package com.example.friendlykeyboard

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.friendlykeyboard.databinding.ActivitySettingsKeyboardSizeBinding

class SettingsKeyboardSizeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsKeyboardSizeBinding
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsKeyboardSizeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        with (supportActionBar!!) {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_action_arrow_back)
            title = "키보드 크기"
        }

        pref = getSharedPreferences("setting", Activity.MODE_PRIVATE)
        editor = pref.edit()

        /*
        val intent = Intent().apply {
            putExtra("height", "높이 50%")
            putExtra("paddingWidth", "좌우 여백 50%")
            putExtra("paddingBottom", "하단 여백 50%")
        }
        */
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
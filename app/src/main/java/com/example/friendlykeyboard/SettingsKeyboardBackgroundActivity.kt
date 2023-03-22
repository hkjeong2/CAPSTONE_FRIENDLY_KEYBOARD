package com.example.friendlykeyboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.friendlykeyboard.databinding.ActivitySettingsKeyboardBackgroundBinding

class SettingsKeyboardBackgroundActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsKeyboardBackgroundBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsKeyboardBackgroundBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val intent = Intent().apply {
                putExtra("background", "배경색 #FFFFFF")
            }
            setResult(400, intent)
            finish()
        }
    }
}
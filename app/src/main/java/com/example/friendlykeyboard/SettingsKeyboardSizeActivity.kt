package com.example.friendlykeyboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.friendlykeyboard.databinding.ActivitySettingsKeyboardSizeBinding

class SettingsKeyboardSizeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsKeyboardSizeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsKeyboardSizeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val intent = Intent().apply {
                putExtra("height", "높이 50%")
                putExtra("paddingWidth", "좌우 여백 50%")
                putExtra("paddingBottom", "하단 여백 50%")
            }
            setResult(100, intent)
            finish()
        }
    }
}
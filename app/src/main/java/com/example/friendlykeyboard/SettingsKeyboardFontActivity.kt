package com.example.friendlykeyboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.friendlykeyboard.databinding.ActivitySettingsKeyboardFontBinding

class SettingsKeyboardFontActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsKeyboardFontBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsKeyboardFontBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val intent = Intent().apply {
                putExtra("size", "크기 50%")
                putExtra("font", "폰트 Bold")
            }
            setResult(200, intent)
            finish()
        }
    }
}
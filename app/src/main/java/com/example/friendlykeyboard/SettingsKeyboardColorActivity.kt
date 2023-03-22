package com.example.friendlykeyboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.friendlykeyboard.databinding.ActivitySettingsKeyboardColorBinding

class SettingsKeyboardColorActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsKeyboardColorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsKeyboardColorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val intent = Intent().apply {
                putExtra("color", "색상 #FFFFFF")
            }
            setResult(300, intent)
            finish()
        }
    }
}
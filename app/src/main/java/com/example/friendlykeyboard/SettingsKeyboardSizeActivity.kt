package com.example.friendlykeyboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.friendlykeyboard.databinding.ActivitySettingsKeyboardSizeBinding

class SettingsKeyboardSizeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsKeyboardSizeBinding

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

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            setResult(RESULT_CANCELED)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
package com.example.friendlykeyboard

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.friendlykeyboard.databinding.ActivitySettingsKeyboardBackgroundBinding
import com.example.friendlykeyboard.keyboard.KeyBoardService

class SettingsKeyboardBackgroundActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsKeyboardBackgroundBinding
    private var selectedColor: Int? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsKeyboardBackgroundBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        with (supportActionBar!!) {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_action_arrow_back)
            title = "키보드 배경색"
        }

        binding.colorPickerView.addOnColorChangedListener {
            selectedColor = binding.colorPickerView.selectedColor
            // TODO: 동반 객체로 설정하면 memory leak 위험이 있음.
            //KeyBoardService.keyboardKorean.koreanLayout.setBackgroundColor(selectedColor!!)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (selectedColor != null) {
            val intent = Intent().apply {
                putExtra("background", "${binding.colorPickerView.selectedColor}")
            }
            setResult(400, intent)
        } else {
            setResult(RESULT_CANCELED)
        }
        finish()
    }
}
package com.example.friendlykeyboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.friendlykeyboard.databinding.ActivitySettingsKeyboardBackgroundBinding
import com.example.friendlykeyboard.keyboard.KeyBoardService

class SettingsKeyboardBackgroundActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsKeyboardBackgroundBinding
    
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

        KeyBoardService().keyboardKorean.koreanLayout.setBackgroundColor(binding.colorPickerView.selectedColor)

        binding.colorPickerView.addOnColorChangedListener {
            // TODO
            //keyboardLayout.linearLayout.setBackgroundColor(binding.colorPickerView.selectedColor)
        }

        /*
        binding.button.setOnClickListener {
            val intent = Intent().apply {
                putExtra("background", "배경색 #FFFFFF")
            }
            setResult(400, intent)
            finish()
        }
        */
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
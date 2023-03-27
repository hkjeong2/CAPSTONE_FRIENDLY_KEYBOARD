package com.example.friendlykeyboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.friendlykeyboard.databinding.ActivitySettingsKeyboardBackgroundBinding

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
            // TODO
            //keyboardLayout.linearLayout.setBackgroundColor(binding.colorPickerView.selectedColor)
            selectedColor = binding.colorPickerView.selectedColor
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
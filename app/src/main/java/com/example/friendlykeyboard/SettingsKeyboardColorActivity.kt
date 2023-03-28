package com.example.friendlykeyboard

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import com.example.friendlykeyboard.databinding.ActivitySettingsKeyboardColorBinding
import com.flask.colorpicker.builder.ColorPickerClickListener

class SettingsKeyboardColorActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsKeyboardColorBinding
    private lateinit var inputMethodManager: InputMethodManager
    private var selectedColor: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsKeyboardColorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        with (supportActionBar!!) {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_action_arrow_back)
            title = "키보드 색상"
        }

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        binding.colorPickerView.addOnColorChangedListener {
            selectedColor = binding.colorPickerView.selectedColor
            binding.textInputEditText.requestFocus()
            inputMethodManager.showSoftInput(binding.textInputEditText, InputMethodManager.SHOW_IMPLICIT)
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
                putExtra("color", "${binding.colorPickerView.selectedColor}")
            }
            setResult(300, intent)
        } else {
            setResult(RESULT_CANCELED)
        }
        finish()
    }
}
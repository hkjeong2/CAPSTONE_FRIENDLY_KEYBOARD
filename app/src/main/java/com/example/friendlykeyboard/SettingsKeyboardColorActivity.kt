package com.example.friendlykeyboard

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import com.example.friendlykeyboard.databinding.ActivitySettingsKeyboardColorBinding

class SettingsKeyboardColorActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsKeyboardColorBinding
    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

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
        pref = getSharedPreferences("setting", Activity.MODE_PRIVATE)
        editor = pref.edit()

        binding.colorPickerView.addOnColorChangedListener {
            editor.putInt("keyboardColor", binding.colorPickerView.selectedColor).apply()
            binding.textInputEditText.requestFocus()
            inputMethodManager.showSoftInput(binding.textInputEditText, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
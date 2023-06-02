package com.example.friendlykeyboard.activities

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.friendlykeyboard.R
import com.example.friendlykeyboard.databinding.ActivitySettingsVibrateBinding

class SettingsVibrateActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsVibrateBinding
    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsVibrateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        with (supportActionBar!!) {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_action_arrow_back)
            title = "키보드 진동"
        }

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        pref = getSharedPreferences("setting", Activity.MODE_PRIVATE)
        editor = pref.edit()

        binding.vibrateSwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
            editor.putBoolean("vibrate", isChecked).apply()
            binding.textInputEditText.requestFocus()
            inputMethodManager.showSoftInput(binding.textInputEditText, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onStart() {
        super.onStart()
        binding.vibrateSwitch.isChecked = pref.getBoolean("vibrate", false)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
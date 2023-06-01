package com.example.friendlykeyboard.activities

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import com.example.friendlykeyboard.R
import com.example.friendlykeyboard.databinding.ActivitySettingsKeyboardSizeBinding

class SettingsKeyboardSizeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsKeyboardSizeBinding
    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

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

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        pref = getSharedPreferences("setting", Activity.MODE_PRIVATE)
        editor = pref.edit()

        initProgresses()
        initListeners()
    }

    private fun initProgresses() {
        binding.heightSeekBar.progress = pref.getInt("keyboardHeight", 150) - 115
        binding.leftPaddingSeekBar.progress = pref.getInt("keyboardPaddingLeft", 0)
        binding.rightPaddingSeekBar.progress = pref.getInt("keyboardPaddingRight", 0)
        binding.bottomPaddingSeekBar.progress = pref.getInt("keyboardPaddingBottom", 0)
    }

    private fun initListeners() {
        binding.heightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                editor.putInt("keyboardHeight", progress + 115).apply()
                binding.textInputEditText.requestFocus()
                inputMethodManager.showSoftInput(binding.textInputEditText, InputMethodManager.SHOW_IMPLICIT)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.leftPaddingSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                editor.putInt("keyboardPaddingLeft", progress).apply()
                binding.textInputEditText.requestFocus()
                inputMethodManager.showSoftInput(binding.textInputEditText, InputMethodManager.SHOW_IMPLICIT)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.rightPaddingSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                editor.putInt("keyboardPaddingRight", progress).apply()
                binding.textInputEditText.requestFocus()
                inputMethodManager.showSoftInput(binding.textInputEditText, InputMethodManager.SHOW_IMPLICIT)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.bottomPaddingSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                editor.putInt("keyboardPaddingBottom", progress).apply()
                binding.textInputEditText.requestFocus()
                inputMethodManager.showSoftInput(binding.textInputEditText, InputMethodManager.SHOW_IMPLICIT)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
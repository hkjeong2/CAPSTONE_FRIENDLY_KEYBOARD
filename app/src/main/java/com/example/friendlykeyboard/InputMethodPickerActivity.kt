package com.example.friendlykeyboard

import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.friendlykeyboard.databinding.ActivityInputMethodPickerBinding

class InputMethodPickerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputMethodPickerBinding
    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var currentInputMethod: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputMethodPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputMethodManager =
            applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        currentInputMethod = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD
        )

        binding.textView.text = buildString {
            append(currentInputMethod)
            append("\n")

            if (currentInputMethod == "com.example.friendlykeyboard/.keyboard.KeyBoardService") {
                append("true")
            } else {
                append("false")
            }
        }

        inputMethodManager.showInputMethodPicker()

        initClickListener()
    }

    private fun initClickListener() {
        binding.inputMethodPickerButton.setOnClickListener {
            inputMethodManager.showInputMethodPicker()
        }
    }
}
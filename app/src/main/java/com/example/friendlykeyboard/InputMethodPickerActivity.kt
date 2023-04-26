package com.example.friendlykeyboard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.friendlykeyboard.databinding.ActivityInputMethodPickerBinding

class InputMethodPickerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputMethodPickerBinding
    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var currentInputMethod: String
    private var state = State.NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputMethodPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val animation = AnimationUtils.loadAnimation(this, R.anim.animation)
        binding.imageView1.startAnimation(animation)

        inputMethodManager =
            applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        Handler(Looper.getMainLooper()).postDelayed({
            inputMethodManager.showInputMethodPicker()
            state = State.PICKING
        }, 300)

        initClickListener()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (state == State.PICKING) {
            state = State.CHOSEN
        } else if (state == State.CHOSEN) {
            state = State.NONE

            currentInputMethod = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.DEFAULT_INPUT_METHOD
            )

            if (currentInputMethod == "com.example.friendlykeyboard/.keyboard.KeyBoardService") {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun initClickListener() {
        binding.inputMethodPickerButton.setOnClickListener {
            inputMethodManager.showInputMethodPicker()
            state = State.PICKING
        }
    }

    enum class State { NONE, PICKING, CHOSEN }
}
package com.example.friendlykeyboard.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.friendlykeyboard.R
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

        // InputMethodManager 인스턴스를 가져옵니다.
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        showKeyboardOption()
        initClickListener()

    }

    private fun showKeyboardOption(){
        // 소프트 키보드의 IME ID를 얻어옵니다.
        val imeId = "com.example.friendlykeyboard/.keyboard.KeyBoardService"
        // 현재 시스템에서 활성화된 모든 입력 메소드를 반환
        val enabledInputMethodIds = inputMethodManager.enabledInputMethodList.map { it.id }
        // 활성화된 키보드 없으면 유저에게 선택하도록 안내
        if (!enabledInputMethodIds.contains(imeId)){
            Toast.makeText(applicationContext, "FriendlyKeyboard를 활성화 해주세요!", Toast.LENGTH_SHORT).show()

            // 설정 화면으로 이동
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                startActivity(intent)
            }, 1000)
        }
        else{
            Handler(Looper.getMainLooper()).postDelayed({
                inputMethodManager.showInputMethodPicker()
                state = State.PICKING
            }, 300)
        }
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
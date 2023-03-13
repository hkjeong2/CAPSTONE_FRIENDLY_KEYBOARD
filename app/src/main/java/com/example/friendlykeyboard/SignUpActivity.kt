package com.example.friendlykeyboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.friendlykeyboard.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.idCheckButton.setOnClickListener {
            // TODO: 아이디 중복 검사
        }

        binding.signUpButton.setOnClickListener {
            // TODO: 아이디 중복 검사, 비밀번호 2차 검사가 잘되었는지 확인해야 함.
            
            val id = binding.editId.text.toString()
            val password = binding.editPwd.text.toString()
            val intent = Intent(this, LoginActivity::class.java).apply {
                putExtra("id", id)
                putExtra("password", password)
            }
            setResult(RESULT_OK, intent)
            finish()
        }

        binding.signInTextView.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}
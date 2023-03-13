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

        binding.signUpButton.setOnClickListener {
            val id = binding.editId.text.toString()
            val password = binding.editPwd.text.toString()
            // TODO: 입력한 값이 정확하면 RESULT_OK, 아니면 RESULT_CANCELED
            val intent = Intent(this, LoginActivity::class.java).apply {
                putExtra("id", id)
                putExtra("password", password)
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
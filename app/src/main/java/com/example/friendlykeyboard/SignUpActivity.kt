package com.example.friendlykeyboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import com.example.friendlykeyboard.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private var idCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 아이디 중복 검사 후 수정하면 idCheck 값을 false 로 재설정 
        binding.editId.addTextChangedListener {
            idCheck = false
        }

        // 실시간으로 비밀번호 일치 여부 확인
        binding.editPwd2.addTextChangedListener {
            if (binding.editPwd.text.toString() != binding.editPwd2.text.toString()) {
                binding.editPwd2.error = "비밀번호가 일치하지 않습니다."
            }
        }

        // 아이디 중복 검사 버튼 이벤트 설정
        binding.idCheckButton.setOnClickListener {
            idCheck = validateId()
        }
        
        // 회원가입 버튼 이벤트 설정
        binding.signUpButton.setOnClickListener {
            if (idCheck) {
                if (validatePassword()) {
                    val id = binding.editId.text.toString()
                    val password = binding.editPwd.text.toString()
                    val intent = Intent(this, LoginActivity::class.java).apply {
                        putExtra("id", id)
                        putExtra("password", password)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            } else {
                binding.editId.error = "아이디 중복 여부를 확인해주세요."
            }
        }

        binding.signInTextView.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    // 아이디 중복 검사
    private fun validateId(): Boolean {
        val id = binding.editId.text.toString()

        if (id.isEmpty()) {
            binding.editId.error = "아이디를 입력해주세요."
            return false
        } else {
            // TODO: 아이디 중복 검사
            // TODO: 서버와 연동
            // TODO: 해당 블록을 else if로 수정할 것.
            // TODO: 해당 블록은 아이디가 중복된 경우
            binding.editId.error = "중복된 아이디입니다."
        }

        return true
    }

    // 비밀번호 검사
    private fun validatePassword(): Boolean {
        val password1 = binding.editPwd.text.toString()
        val password2 = binding.editPwd2.text.toString()

        if (password1.isEmpty()) {
            binding.editPwd.error = "비밀번호를 입력해주세요."
            return false
        } else if (password2.isEmpty()) {
            binding.editPwd2.error = "비밀번호를 한 번 더 입력해주세요."
            return false
        } else if (password1 != password2) {
            binding.editPwd2.error = "비밀번호가 일치하지 않습니다."
            return false
        } else {
            return true
        }
    }
}
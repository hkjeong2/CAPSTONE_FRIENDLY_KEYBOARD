package com.example.friendlykeyboard

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.friendlykeyboard.databinding.ActivitySignUpBinding
import com.example.friendlykeyboard.retrofit_util.Account
import com.example.friendlykeyboard.retrofit_util.DataModel
import com.example.friendlykeyboard.retrofit_util.RetrofitClient

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private var idCheck = false
    private val service = RetrofitClient.getApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT > 9) {
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

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

                    val result: DataModel?
                    val account = Account(id, password)

                    try {
                        val response = service.signUp(account).execute()
                        if (response.isSuccessful) {
                            result = response.body()

                            when (result?.responseText) {
                                "Success" -> {
                                    Toast.makeText(
                                        applicationContext,
                                        "회원가입이 완료되었습니다.",
                                        Toast.LENGTH_SHORT).show()

                                    val intent = Intent(this, LoginActivity::class.java).apply {
                                        putExtra("id", id)
                                        putExtra("password", password)
                                    }
                                    setResult(RESULT_OK, intent)
                                    finish()
                                }
                                else -> {
                                    Toast.makeText(
                                        applicationContext,
                                        "회원가입이 실패하였습니다.",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Log.d("SignUpActivity", response.message())
                            // 통신이 실패한 경우
                            Toast.makeText(
                                applicationContext,
                                "오류가 발생하였습니다.",
                                Toast.LENGTH_SHORT).show()
                            setResult(RESULT_CANCELED)
                            finish()
                        }
                    } catch (e: Exception) {
                        // 통신 실패 (인터넷 끊김, 예외 발생 등 시스템적인 이유
                        e.printStackTrace()
                        Toast.makeText(
                            applicationContext,
                            "서버와의 통신이 실패하였습니다.",
                            Toast.LENGTH_SHORT).show()
                        setResult(RESULT_CANCELED)
                        finish()
                    }
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
        }

        val result: DataModel?
        val account = Account(id, "?")

        try {
            val response = service.getAccount(account).execute()
            if (response.isSuccessful) {
                result = response.body()

                when (result?.responseText) {
                    "Unavailable" -> {
                        binding.editId.error = "중복된 아이디입니다."
                        Toast.makeText(
                            applicationContext,
                            "중복된 아이디입니다.",
                            Toast.LENGTH_SHORT).show()
                        return false
                    }
                    "Available" -> {
                        Toast.makeText(
                            applicationContext,
                            "사용가능한 아이디입니다.",
                            Toast.LENGTH_SHORT).show()
                        return true
                    }
                    else -> {
                        Toast.makeText(
                            applicationContext,
                            "?",
                            Toast.LENGTH_SHORT).show()
                        return true
                    }
                }
            } else {
                Log.d("SignUpActivity", response.message())
                // 통신이 실패한 경우
                Toast.makeText(
                    applicationContext,
                    "오류가 발생하였습니다.",
                    Toast.LENGTH_SHORT).show()
                setResult(RESULT_CANCELED)
                finish()
            }
        } catch (e: Exception) {
            // 통신 실패 (인터넷 끊김, 예외 발생 등 시스템적인 이유
            e.printStackTrace()
            Toast.makeText(
                applicationContext,
                "서버와의 통신이 실패하였습니다.",
                Toast.LENGTH_SHORT).show()
            //setResult(RESULT_CANCELED)
            //finish()
        }

        return false
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
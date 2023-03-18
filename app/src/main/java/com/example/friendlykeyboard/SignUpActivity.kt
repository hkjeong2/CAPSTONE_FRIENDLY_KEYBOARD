package com.example.friendlykeyboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.friendlykeyboard.databinding.ActivitySignUpBinding
import com.example.friendlykeyboard.retrofit_util.Account
import com.example.friendlykeyboard.retrofit_util.DataModel
import com.example.friendlykeyboard.retrofit_util.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private var idCheck = false
    private val service = RetrofitClient.getApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 실시간으로 비밀번호 일치 여부 확인
        binding.editPwd2.addTextChangedListener {
            if (binding.editPwd.text.toString() != binding.editPwd2.text.toString()) {
                binding.editPwd2.error = "비밀번호가 일치하지 않습니다."
            }
        }

        // 아이디 중복 검사 버튼 이벤트 설정
        binding.idCheckButton.setOnClickListener {
            validateId()
        }
        
        // 회원가입 버튼 이벤트 설정
        binding.signUpButton.setOnClickListener {
            if (idCheck) {
                if (validatePassword()) {
                    val id = binding.editId.text.toString()
                    val password = binding.editPwd.text.toString()

                    val account = Account(id, password)

                    service.signUp(account).enqueue(object : Callback<DataModel> {
                        override fun onResponse(
                            call: Call<DataModel>,
                            response: Response<DataModel>
                        ) {
                            if (response.isSuccessful) {
                                val result = response.body()

                                when (result?.responseText) {
                                    "Success" -> {
                                        Toast.makeText(
                                            applicationContext,
                                            "회원가입이 완료되었습니다.",
                                            Toast.LENGTH_SHORT).show()

                                        val intent = Intent(this@SignUpActivity, LoginActivity::class.java).apply {
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
                                // 통신이 실패한 경우
                                Log.d("SignUpActivity", response.message())
                                Toast.makeText(
                                    applicationContext,
                                    "오류가 발생하였습니다.",
                                    Toast.LENGTH_SHORT).show()
                                setResult(RESULT_CANCELED)
                                finish()
                            }
                        }

                        override fun onFailure(call: Call<DataModel>, t: Throwable) {
                            // 통신 실패 (인터넷 끊김, 예외 발생 등 시스템적인 이유
                            t.printStackTrace()
                            Toast.makeText(
                                applicationContext,
                                "서버와의 통신이 실패하였습니다.",
                                Toast.LENGTH_SHORT).show()
                            setResult(RESULT_CANCELED)
                            finish()
                        }
                    })
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
    private fun validateId() {
        val id = binding.editId.text.toString()

        if (id.isEmpty()) {
            binding.editId.error = "아이디를 입력해주세요."
            idCheck = false
            return
        }

        val account = Account(id, "?")

        service.getAccount(account).enqueue(object : Callback<DataModel> {
            override fun onResponse(call: Call<DataModel>, response: Response<DataModel>) {
                if (response.isSuccessful) {
                    val result = response.body()

                    when (result?.responseText) {
                        "Available" -> {
                            Toast.makeText(
                                applicationContext,
                                "사용가능한 아이디입니다.",
                                Toast.LENGTH_SHORT).show()
                            binding.editId.isFocusable = false
                            binding.idCheckButton.isClickable = false
                            idCheck = true
                            return
                        }
                        else -> {
                            binding.editId.error = "중복된 아이디입니다."
                            Toast.makeText(
                                applicationContext,
                                "중복된 아이디입니다.",
                                Toast.LENGTH_SHORT).show()
                            idCheck = false
                            return
                        }
                    }
                } else {
                    // 통신이 실패한 경우
                    Log.d("SignUpActivity", response.message())
                    Toast.makeText(
                        applicationContext,
                        "오류가 발생하였습니다.",
                        Toast.LENGTH_SHORT).show()
                    setResult(RESULT_CANCELED)
                    finish()
                }
            }

            override fun onFailure(call: Call<DataModel>, t: Throwable) {
                // 통신 실패 (인터넷 끊김, 예외 발생 등 시스템적인 이유
                t.printStackTrace()
                Toast.makeText(
                    applicationContext,
                    "서버와의 통신이 실패하였습니다.",
                    Toast.LENGTH_SHORT).show()
                //setResult(RESULT_CANCELED)
                //finish()
            }
        })
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
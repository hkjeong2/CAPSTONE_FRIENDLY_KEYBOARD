package com.example.friendlykeyboard.activities

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.friendlykeyboard.databinding.ActivityLoginBinding
import com.example.friendlykeyboard.retrofit_util.Account
import com.example.friendlykeyboard.retrofit_util.AccountDataModel
import com.example.friendlykeyboard.retrofit_util.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val service = RetrofitClient.getApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpPreference()
        autoFillInfo()
        initClickListener()
    }

    private fun setUpPreference() {
        val spfAuto = getSharedPreferences("cbAuto", 0)
        if (spfAuto.getBoolean("check", false)){
            val currentInputMethod = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.DEFAULT_INPUT_METHOD
            )

            // 현재 키보드가 friendly keyboard 일 경우 바로 홈 화면으로 이동
            if (currentInputMethod == "com.example.friendlykeyboard/.keyboard.KeyBoardService") {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, InputMethodPickerActivity::class.java))
                finish()
            }
        } else {
            val id = spfAuto.getString("id", "")
            val pwd = spfAuto.getString("pwd", "")
            binding.editId.setText(id)
            binding.editPwd.setText(pwd)
        }
    }

    private fun autoFillInfo(){
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                val intent = it.data!!
                val id = intent.getStringExtra("id")
                val password = intent.getStringExtra("password")
                binding.editId.setText(id)
                binding.editPwd.setText(password)
            } else {
                binding.editId.setText("")
                binding.editPwd.setText("")
            }
        }
    }

    private fun initClickListener(){
        binding.signUpButton.setOnClickListener {
            resultLauncher.launch(Intent(this, SignUpActivity::class.java))
        }

        binding.loginButton.setOnClickListener {
            //로그인 성공 시 main 화면으로 이동
            login()
        }
    }

    private fun editSPF(id : String, pwd : String){
        val spfID = getSharedPreferences("cbAuto", 0)
        val editor = spfID.edit()
        editor.putBoolean("check", true)
        editor.putString("id", id)
        editor.putString("pwd", pwd)
        editor.apply()
    }

    private fun login() {
        val id = binding.editId.text.toString()
        val pwd = binding.editPwd.text.toString()

        if (id.isEmpty()){
            binding.editId.error = "아이디를 입력해주세요"
            return
        } else if (pwd.isEmpty()){
            binding.editPwd.error = "패스워드를 입력해주세요"
            return
        } else {
            val account = Account(id, pwd)

            service.signIn(account).enqueue(object : Callback<AccountDataModel> {
                override fun onResponse(call: Call<AccountDataModel>, response: Response<AccountDataModel>) {
                    if (response.isSuccessful) {
                        val result = response.body()

                        when (result?.responseText) {
                            "Available" -> {
                                editSPF(
                                    binding.editId.text.toString(),
                                    binding.editPwd.text.toString()
                                )

                                val currentInputMethod = Settings.Secure.getString(
                                    contentResolver,
                                    Settings.Secure.DEFAULT_INPUT_METHOD
                                )

                                // 현재 키보드가 friendly keyboard 일 경우 바로 홈 화면으로 이동
                                if (currentInputMethod == "com.example.friendlykeyboard/.keyboard.KeyBoardService") {
                                    //본격적으로 main 화면 시작
                                    Toast.makeText(this@LoginActivity, "로그인에 성공하였습니다", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                    finish()
                                } else {
                                    startActivity(Intent(this@LoginActivity, InputMethodPickerActivity::class.java))
                                    finish()
                                }
                            }
                            else -> {
                                Toast.makeText(
                                    applicationContext,
                                    "일치하는 정보가 없습니다.",
                                    Toast.LENGTH_SHORT).show()
                                binding.editId.setText("")
                                binding.editPwd.setText("")
                                return
                            }
                        }
                    } else {
                        // 통신이 실패한 경우
                        Log.d("LoginActivity", response.message())
                        Toast.makeText(
                            applicationContext,
                            "오류가 발생하였습니다.",
                            Toast.LENGTH_SHORT).show()
                        return
                    }
                }

                override fun onFailure(call: Call<AccountDataModel>, t: Throwable) {
                    // 통신 실패 (인터넷 끊김, 예외 발생 등 시스템적인 이유)
                    t.printStackTrace()
                    Toast.makeText(
                        applicationContext,
                        "서버와의 통신이 실패하였습니다.",
                        Toast.LENGTH_SHORT).show()
                    return
                }
            })
        }
    }
}
package com.example.friendlykeyboard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.friendlykeyboard.databinding.ActivityLoginBinding
import com.example.friendlykeyboard.retrofit_util.Account
import com.example.friendlykeyboard.retrofit_util.DataModel
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

    private fun setUpPreference(){
        //자동로그인 체크돼 있을 시 바로 메인 화면으로 전환
        val spfAuto = getSharedPreferences("cbAuto", 0)
        if (spfAuto.getBoolean("check", false)){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        //화면 setup 후 ID저장 checkbox 값 확인
        val spfID = getSharedPreferences("cbID", 0)
        //checkbox on 일 시 저장된 id 및 pwd로 editbox set up
        if (spfID.getBoolean("check", false)){
            binding.checkboxSaveID.isChecked = true
            val id = spfID.getString("id", null)
            val pwd = spfID.getString("pwd", null)

            if (id == null || pwd == null)
                return

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

        binding.checkboxSaveID.setOnClickListener {
            //ID 저장 한 번이라도 해제 시 내용 삭제
            if (!binding.checkboxSaveID.isChecked){
                editSPF("cbID", false, "", "")
            }
        }

        binding.checkboxAutoLogin.setOnClickListener{
            //자동로그인 한 번이라도 해제 시 내용 삭제
            if (!binding.checkboxAutoLogin.isChecked){
                editSPF("cbAuto", false, "", "")
            }
        }


    }

    private fun editSPF(name : String, check : Boolean, id : String, pwd : String){
        val spfID = getSharedPreferences(name, 0)
        val editor = spfID.edit()
        editor.putBoolean("check", check)
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

            service.signIn(account).enqueue(object : Callback<DataModel> {
                override fun onResponse(call: Call<DataModel>, response: Response<DataModel>) {
                    if (response.isSuccessful) {
                        val result = response.body()

                        when (result?.responseText) {
                            "Available" -> {
                                //ID 저장 checkbox 눌려 있는 상태로 로그인 성공 --> 정보 저장
                                if (binding.checkboxSaveID.isChecked){
                                    editSPF("cbID", true, binding.editId.text.toString(), binding.editPwd.text.toString())
                                }
                                //자동 로그인 checkbox 눌린 상태 로그인 성공 --> 자동 로그인 정보 저장
                                if (binding.checkboxAutoLogin.isChecked){
                                    editSPF("cbAuto", true, "", "")
                                }
                                //본격적으로 main 화면 시작
                                Toast.makeText(this@LoginActivity, "로그인에 성공하였습니다", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
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
                        Toast.makeText(
                            applicationContext,
                            "오류가 발생하였습니다.",
                            Toast.LENGTH_SHORT).show()
                        return
                    }
                }

                override fun onFailure(call: Call<DataModel>, t: Throwable) {
                    // 통신 실패 (인터넷 끊김, 예외 발생 등 시스템적인 이유
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
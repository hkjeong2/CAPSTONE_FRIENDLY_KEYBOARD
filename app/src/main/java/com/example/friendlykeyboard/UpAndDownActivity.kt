package com.example.friendlykeyboard

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.friendlykeyboard.databinding.ActivityUpAndDownBinding
import com.example.friendlykeyboard.retrofit_util.Account
import com.example.friendlykeyboard.retrofit_util.HateSpeechCountDataModel
import com.example.friendlykeyboard.retrofit_util.HateSpeechDataModel
import com.example.friendlykeyboard.retrofit_util.RetrofitClient
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpAndDownActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUpAndDownBinding
    private lateinit var spf : SharedPreferences
    private lateinit var curse : String
    private var answer : Int = 0
    private val list : List<String> = listOf("여성/가족","남성","성소수자","인종/국적","연령","지역","종교","기타","악플/욕설")
    private var randomIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpAndDownBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSetting()
        initScreen()
        initClickListener()
        initAnswer()

    }

    private fun initSetting(){
        spf = getSharedPreferences("setting", 0)
        curse = intent.getStringExtra("curse")!!

    }

    private fun initScreen(){
        randomIndex = (list.indices).random()
        val randomWords = list[randomIndex]
        var words = "\"" + randomWords + "\""
        if (randomWords != "악플/욕설"){
            words += " 혐오적인"
        }
        val spannableString = SpannableString(words)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, randomWords!!.length + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.textViewMain.text = spannableString
    }

    private fun initClickListener(){
        binding.inputButton.setOnClickListener{
            initStage()
            CoroutineScope(Dispatchers.Main).launch{
                delay(500)
                Toast.makeText(applicationContext, "미션 성공", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun initStage(){
        spf.edit().putInt("stageNumber", 0).apply()

        //기능설정 색깔 복구
        spf.edit().putInt("settingAlarmColor", Color.parseColor("#000000")).apply()
        spf.edit().putInt("settingInvisibleColor", Color.parseColor("#000000")).apply()
        spf.edit().putInt("settingEnglishColor", Color.parseColor("#000000")).apply()
        spf.edit().putInt("settingRandomColor", Color.parseColor("#000000")).apply()
        spf.edit().putInt("settingCorrectColor", Color.parseColor("#000000")).apply()

        // 키보드 폰트색 복구
        spf.edit().putInt("keyboardFontColor", spf.getInt("tempKeyboardFontColor", 0)).apply()
    }

    private fun initAnswer(){
        val accountID = getSharedPreferences("cbAuto", 0).getString("id", "")!!
        val account = Account(accountID, "?")
        val service = RetrofitClient.getApiService()

        service.getHateSpeechCountsCall(account).enqueue(object : Callback<HateSpeechCountDataModel> {
            override fun onResponse(
                call: Call<HateSpeechCountDataModel>,
                response: Response<HateSpeechCountDataModel>
            ) {
                if (response.isSuccessful){
                    val result = response.body()!!
                    var counts = mapOf<String, Int>()

                    when (randomIndex){
                        0 -> counts = result.count1
                        1 -> counts = result.count2
                        2 -> counts = result.count3
                        3 -> counts = result.count4
                        4 -> counts = result.count5
                        5 -> counts = result.count6
                        6 -> counts = result.count7
                        7 -> counts = result.count8
                        8 -> counts = result.count9
                    }

                    var sum = 0
                    for (key in counts.keys) {
                        sum += counts[key] ?: 0
                    }
                    answer = sum

                    Log.d("result", counts.toString())
                    Log.d("result", answer.toString())
                }
                else{
                    Log.d("UpAndDownActivity", response.message())
                    Toast.makeText(applicationContext,"오류가 발생하였습니다.",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<HateSpeechCountDataModel>, t: Throwable) {
                // 통신 실패 (인터넷 끊김, 예외 발생 등 시스템적인 이유)
                t.printStackTrace()
                Toast.makeText(applicationContext,"서버와의 통신이 실패하였습니다.",Toast.LENGTH_SHORT).show()
            }

        })
    }

}
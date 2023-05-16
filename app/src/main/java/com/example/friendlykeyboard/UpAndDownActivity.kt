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
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.friendlykeyboard.databinding.ActivityUpAndDownBinding
import com.example.friendlykeyboard.retrofit_util.Account
import com.example.friendlykeyboard.retrofit_util.HateSpeechCountDataModel
import com.example.friendlykeyboard.retrofit_util.RetrofitClient
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*


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
        initAnswer()
        initClickListener()

    }

    private fun initSetting(){
        spf = getSharedPreferences("setting", 0)
        curse = intent.getStringExtra("curse")!!
    }

    private fun initScreen(){
//        randomIndex = (list.indices).random()
        randomIndex = Random().nextInt(list.size)
        val randomWords = list[randomIndex]
        var words = "\"" + randomWords + "\""
        if (randomWords != "악플/욕설"){
            words += " 혐오적인"
        }
        val spannableString = SpannableString(words)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, randomWords!!.length + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.textViewMain.text = spannableString
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

    private fun initClickListener(){
        // 숫자 입력 시
        binding.inputButton.setOnClickListener{
            checkAnswer()
        }
        // 정답 처리
        binding.understandCheckBox.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (isChecked){
                binding.understandCheckBox.isClickable = false

                initStage()
                CoroutineScope(Dispatchers.Main).launch{
                    delay(500)
                    Toast.makeText(applicationContext, "미션 성공", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun checkAnswer(){
        val text = binding.editText.text
        binding.editText.setText("")

        if (text.toString() == ""){
            binding.inputNumber.text= ""
            binding.updown.text = "숫자를 입력해주세요!"
            binding.inputNumber.background = ContextCompat.getDrawable(this, R.drawable.circle)
            setVisibility(false)
        }
        else{
            binding.inputNumber.text = text
            val inputNumber = text.toString().toInt()
            val shake: Animation = AnimationUtils.loadAnimation(this, R.anim.shake)

            //틀렸을 시
            if (inputNumber > answer){
                binding.updown.text = "Down"
                binding.inputNumber.background = ContextCompat.getDrawable(this, R.drawable.circle_wrong)
                binding.inputNumber.startAnimation(shake)
                setVisibility(false)
            }
            else if (inputNumber < answer){
                binding.updown.text = "Up"
                binding.inputNumber.background = ContextCompat.getDrawable(this, R.drawable.circle_wrong)
                binding.inputNumber.startAnimation(shake)
                setVisibility(false)
            }
            else{   //정답일 시
                binding.updown.text = "정답!"
                binding.inputNumber.background = ContextCompat.getDrawable(this, R.drawable.circle_correct)
                val sentence = "내가 " + list[randomIndex] + " 혐오적인 표현을 " + answer + "번 사용했어. " +
                        "앞으로 더 이상 이런 말을 쓰지 않고 언어 습관을 고칠 수 있도록 짧고 강하게 충고해줘"
                loadResponse(sentence)
                binding.advice.text = "피드백 생성중..."
                binding.cardView3.visibility = View.VISIBLE
                binding.advice.visibility = View.VISIBLE
            }
        }
    }

    private fun loadResponse(sentence : String){
        //chatGPT 호출
        var client = OkHttpClient()
        val arr = JSONArray()
        val userMsg = JSONObject()

        try {
            //유저 메세지
            userMsg.put("role", "user")
            userMsg.put("content", sentence)
            //array에 담아서 한번에
            arr.put(userMsg)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        val obj = JSONObject()
        try {
            //모델명 변경
            obj.put("model", "gpt-3.5-turbo")
            obj.put("messages", arr)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = obj.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer " + BuildConfig.MY_KEY)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            @Throws(IOException::class)
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    var jsonObject: JSONObject? = null
                    try {
                        jsonObject = JSONObject(response.body!!.string())
                        val jsonArray = jsonObject.getJSONArray("choices")
                        val result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content")

                        CoroutineScope(Dispatchers.Main).launch {
                            // ChatGPT 응답 결과 처리
                            binding.advice.text = "\"" + result + "\""
                            delay(3000)
                            setVisibility(true)
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                } else {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(applicationContext,"api 오류가 발생하였습니다.",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(applicationContext,"api 통신이 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setVisibility(flag : Boolean){
        if (flag){
            binding.cardView3.visibility = View.VISIBLE
            binding.advice.visibility = View.VISIBLE
            binding.understand.visibility = View.VISIBLE
            binding.understandCheckBox.visibility = View.VISIBLE
        }
        else {
            binding.cardView3.visibility = View.INVISIBLE
            binding.advice.visibility = View.INVISIBLE
            binding.understand.visibility = View.INVISIBLE
            binding.understandCheckBox.visibility = View.INVISIBLE
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


}
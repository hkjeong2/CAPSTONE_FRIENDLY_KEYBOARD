package com.example.friendlykeyboard

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.friendlykeyboard.databinding.ActivityChattingBinding
import com.example.friendlykeyboard.retrofit_util.Account
import com.example.friendlykeyboard.retrofit_util.Chat
import com.example.friendlykeyboard.retrofit_util.RetrofitClient
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChattingActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChattingBinding
    private lateinit var chattingRVAdapter: ChattingRVAdapter
    private lateinit var spf : SharedPreferences
    private val service = RetrofitClient.getApiService()
    private var chattingList = arrayListOf<Array<Any>>()
    private var missionText : String = ""
    private val missionCount = 3
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        spf = getSharedPreferences("setting", 0)

        // chatting 내역 view로 가져오기
        runBlocking {
            initChatListData()
        }
        // chatGPT로 순화된 표현 가져오기
        loadMissionText()
        // editText 엔터 시 처리
        initListener()

    }

    private fun initRecyclerView(chatList : ArrayList<Array<Any>>){
        chattingRVAdapter = ChattingRVAdapter(chatList)
        binding.rvChatting.adapter = chattingRVAdapter
        val manager = LinearLayoutManager(applicationContext)
        manager.stackFromEnd = true
        binding.rvChatting.layoutManager = manager
        binding.rvChatting.layoutManager!!.scrollToPosition(chattingRVAdapter.itemCount - 1)
    }

    private suspend fun initChatListData(){
        val id = getSharedPreferences("cbAuto", 0).getString("id", "")!!

        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            try {
                val response = service.getChatList(Account(id, "?"))

                if (response.isSuccessful) {
                    val result = response.body()!!
                    val idList = result.idList
                    val textList = result.textList
                    val dateList = result.dateList

                    chattingList = arrayListOf<Array<Any>>().apply {
                        for (i in idList.indices) {
                            add(arrayOf(idList[i], textList[i], dateList[i]))
                        }
                    }

                    //Recyclerview 초기화
                    initRecyclerView(chattingList)

                } else {
                    // 통신이 실패한 경우
                    Log.d("ChattingActivity", response.message())
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            applicationContext,
                            "오류가 발생하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.d("ChattingActivity", "Connection Error")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        applicationContext,
                        "서버와의 통신이 실패하였습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        /*
        chattingList.add(arrayOf(2, "욕설하지 마세요", currentTime))
        chattingList.add(arrayOf(1, "죄송합니다ㅠㅠ", currentTime))
        chattingList.add(arrayOf(1, "욕설하지 마세요", currentTime))
        chattingList.add(arrayOf(1, "죄송합니다ㅠㅠ", currentTime))
        chattingList.add(arrayOf(2, "욕설하지 마세요", currentTime))
        chattingList.add(arrayOf(1, "죄송합니다ㅠㅠ", currentTime))
        chattingList.add(arrayOf(1, "죄송합니다ㅠㅠ", currentTime))
        chattingList.add(arrayOf(1, "죄송합니다ㅠㅠ", currentTime))
        */

        initRecyclerView(chattingList)
    }

    private fun loadMissionText(){
        // 영문 모드일 시 고려
        if (spf.getInt("stageNumber", 0) == 3)
            missionText = "very sorry"
        else
            missionText = "죄송합니다"
        val text = "[" + missionText + "]를 " + missionCount + "번 입력하세요 !"

        runBlocking {
            addAndnotifyAdapter(2, text)
        }
    }

    private fun initListener(){
        binding.textInputEditText.setOnEditorActionListener { v, actionId, event ->

            //엔터시 actionId = 0
            //EditorInfo.IME_ACTION_SEND = 4
            // 엔터키를 눌렀을 때 실행할 동작을 여기에 작성합니다.
            if (actionId == 0 && binding.textInputEditText.text.toString().isNotEmpty()) {

                val enteredText = binding.textInputEditText.text.toString()
                binding.textInputEditText.setText("")

                //입력된 text 서버에 저장 및 recyclerview에 notify하여 view 변경
                runBlocking {
                    addAndnotifyAdapter(1, enteredText)
                }

                // 과제 성공했는지 검사
                checkMissionAccomplished(enteredText)

                true
            } else false
        }
    }

    private fun checkMissionAccomplished(enteredText : String){
        if (enteredText.equals(missionText)){
            count++
            if (count == missionCount - 1){
                runBlocking {
                    addAndnotifyAdapter(2, "마지막 한 번!")
                }
            }
            if (count == missionCount){
                count = 0
                initStage()
                Toast.makeText(applicationContext, "당신은 용서받았습니다", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.Main).launch{
                    delay(1000)
                    finish()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private suspend fun addAndnotifyAdapter(id : Int, text : String) {
        val accountID = getSharedPreferences("cbAuto", 0).getString("id", "")!!
        val currentTime = currentTime()
        val chat = Chat(accountID, id, text, currentTime)

        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            try {
                val response = service.saveChat(chat)
                if (response.isSuccessful) {
                    Log.d("ChattingActivity", "Saved successfully.")
                } else {
                    // 통신이 실패한 경우
                    Log.d("ChattingActivity", response.message())
                }
            } catch (e: Exception) {
                // 통신 실패 (인터넷 끊김, 예외 발생 등 시스템적인 이유)
                e.printStackTrace()
            }
        }

        chattingList.add(arrayOf(id, text, currentTime))

        //recyclerview에 notify하여 view 변경
        binding.rvChatting.adapter!!.notifyDataSetChanged()
        binding.rvChatting.layoutManager!!.scrollToPosition(chattingRVAdapter.itemCount - 1)
    }

    private fun currentTime() : String {
        /*
        val calendar: Calendar = Calendar.getInstance() // 캘린더 객체 인스턴스 calendar
        val dateFormat = SimpleDateFormat("HH:mm") // SimpleDataFormat 이라는 날짜와 시간을 출력하는 객체 생성, hh을 HH로 변경했더니 24시각제로 바뀜
        return dateFormat.format(calendar.time) // 캘린더 날짜시간 값을 가져와서 문자열인 datatime 으로 변환함
        */
        return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREAN).format(Date())
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
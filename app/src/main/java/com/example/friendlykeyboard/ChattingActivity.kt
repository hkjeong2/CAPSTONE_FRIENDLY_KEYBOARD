package com.example.friendlykeyboard

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.friendlykeyboard.databinding.ActivityChattingBinding
import com.example.friendlykeyboard.retrofit_util.Chat
import com.example.friendlykeyboard.retrofit_util.ChatDataModel
import com.example.friendlykeyboard.retrofit_util.RetrofitClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        initChatListData()
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

    private fun initChatListData(){

        chattingList.add(arrayOf(2, "욕설하지 마세요", "20230507"))
        chattingList.add(arrayOf(1, "죄송합니다ㅠㅠ", "20230507"))
        chattingList.add(arrayOf(1, "욕설하지 마세요", "20230507"))
        chattingList.add(arrayOf(1, "죄송합니다ㅠㅠ", "20230507"))
        chattingList.add(arrayOf(2, "욕설하지 마세요", "20230507"))
        chattingList.add(arrayOf(1, "죄송합니다ㅠㅠ", "20230507"))
        chattingList.add(arrayOf(1, "죄송합니다ㅠㅠ", "20230507"))
        chattingList.add(arrayOf(1, "죄송합니다ㅠㅠ", "20230507"))
        initRecyclerView(chattingList)

        val id = getSharedPreferences("cbAuto", 0).getString("id", "")!!
        service.getChatList(Chat(id)).enqueue(object : Callback<ChatDataModel>{
            override fun onResponse(call: Call<ChatDataModel>, response: Response<ChatDataModel>) {
                if (response.isSuccessful){
                    val result = response.body()

                    //서버로부터 chatting list 받아오기
                    chattingList = result!!.chatList

                    //Recyclerview 초기화
                    initRecyclerView(chattingList)
                }
                else {
                    // 통신이 실패한 경우
                    Toast.makeText(applicationContext, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ChatDataModel>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(
                    applicationContext,
                    "통신이 실패하였습니다.",
                    Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun loadMissionText(){
        // 영문 모드일 시 고려
        if (spf.getInt("stageNumber", 0) == 3)
            missionText = "very sorry"
        else
            missionText = "죄송합니다"
        val text = "'" + missionText + "'를 " + missionCount + "번 입력하세요 !"

        chattingList.add(arrayOf(2, text, currentTime()))
        notifyAdapter()
    }

    private fun initListener(){
        binding.textInputEditText.setOnEditorActionListener { v, actionId, event ->

            //엔터시 actionId = 0
            //EditorInfo.IME_ACTION_SEND = 4
            // 엔터키를 눌렀을 때 실행할 동작을 여기에 작성합니다.
            if (actionId == 0 && binding.textInputEditText.text.toString().isNotEmpty()) {

                val enteredText = binding.textInputEditText.text.toString()
                binding.textInputEditText.setText("")

                // add할 때 서버에 저장해야함
                chattingList.add(arrayOf(1, enteredText, currentTime()))

                //recyclerview에 notify하여 view 변경
                notifyAdapter()

                // 과제 성공했는지 검사
                checkMissionAccomplished(enteredText)

                true
            } else false
        }
    }

    private fun checkMissionAccomplished(enteredText : String){
        if (enteredText.equals(missionText)){
            count++
            if (count == missionCount){
                count = 0
                initStage()
                Toast.makeText(applicationContext, "당신은 용서받았습니다", Toast.LENGTH_SHORT).show()
                GlobalScope.launch{
                    delay(1000)
                    finish()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyAdapter(){
        //recyclerview에 notify하여 view 변경
        binding.rvChatting.adapter!!.notifyDataSetChanged()
        binding.rvChatting.layoutManager!!.scrollToPosition(chattingRVAdapter.itemCount - 1)
    }

    private fun currentTime() : String {
        val calendar: Calendar = Calendar.getInstance() // 캘린더 객체 인스턴스 calendar
        val dateFormat = SimpleDateFormat("HH:mm") // SimpleDataFormat 이라는 날짜와 시간을 출력하는 객체 생성, hh을 HH로 변경했더니 24시각제로 바뀜
        return dateFormat.format(calendar.time) // 캘린더 날짜시간 값을 가져와서 문자열인 datatime 으로 변환함
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
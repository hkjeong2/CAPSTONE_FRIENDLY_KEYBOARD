package com.example.friendlykeyboard

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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

class ChattingActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChattingBinding
    private lateinit var chattingRVAdapter: ChattingRVAdapter
    private lateinit var spf : SharedPreferences
    private val service = RetrofitClient.getApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initChatListData()

        spf = getSharedPreferences("setting", 0)

        binding.button.setOnClickListener {
            initStage()
            Toast.makeText(this, "stage : " + spf.getInt("stageNumber", 0).toString(), Toast.LENGTH_SHORT).show()

            GlobalScope.launch{
                delay(1000)
                finish()
            }
        }

    }

    private fun initRecyclerView(chatList : ArrayList<Array<Any>>){
        chattingRVAdapter = ChattingRVAdapter(chatList)
        binding.rvChatting.adapter = chattingRVAdapter
        val manager = LinearLayoutManager(applicationContext)
        manager.stackFromEnd = true
        manager.scrollToPositionWithOffset(chattingRVAdapter.getItemCount() - 1, 0);
        binding.rvChatting.layoutManager = manager
    }

    private fun initChatListData(){
        var chattingList = arrayListOf<Array<Any>>()

        chattingList.add(arrayOf(2, "욕설하지 마세요", "20230507"))
        chattingList.add(arrayOf(1, "죄송합니다ㅋ", "20230507"))
        chattingList.add(arrayOf(1, "욕설하지 마세요", "20230507"))
        chattingList.add(arrayOf(1, "죄송합니다ㄴ", "20230507"))
        chattingList.add(arrayOf(2, "욕설하지 마세요욕설하지 마세요욕설하지 마세요욕설하지 마세요욕설하지 마세요", "20230507"))
        chattingList.add(arrayOf(2, "죄송합니다ㄷ", "20230507"))
        chattingList.add(arrayOf(2, "욕설하지 마세요", "20230507"))
        chattingList.add(arrayOf(1, "죄송합니다ㄹ", "20230507"))
        chattingList.add(arrayOf(1, "욕설하지 마세요욕설하지 마세요욕설하지 마세요욕설하지 마세요욕설하지 마세요", "20230507"))
        chattingList.add(arrayOf(2, "욕설하지 마세요", "20230507"))
        chattingList.add(arrayOf(1, "죄송합니다ㅎ", "20230507"))
        chattingList.add(arrayOf(2, "욕설하지 마세요", "20230507"))
        chattingList.add(arrayOf(1, "죄송합니다ㅅ", "20230507"))
        chattingList.add(arrayOf(2, "욕설하지 마세요", "20230507"))
        chattingList.add(arrayOf(1, "죄송합니다ㅎ", "20230507"))
        chattingList.add(arrayOf(2, "욕설하지 마세요", "20230507"))
        chattingList.add(arrayOf(1, "죄송합니다ㅅ", "20230507"))
        chattingList.add(arrayOf(2, "욕설하지 마세요", "20230507"))
        chattingList.add(arrayOf(1, "죄송합니다ㅎ", "20230507"))
        chattingList.add(arrayOf(2, "욕설하지 마세요", "20230507"))
        chattingList.add(arrayOf(1, "죄송합니다ㅅ", "20230507"))
        initRecyclerView(chattingList)

        val id = getSharedPreferences("cbAuto", 0).getString("id", "")!!
        service.getChatList(Chat(id)).enqueue(object : Callback<ChatDataModel>{
            override fun onResponse(call: Call<ChatDataModel>, response: Response<ChatDataModel>) {
                if (response.isSuccessful){
                    val result = response.body()

                    //chatting list 받아오기
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

    private fun initStage(){
        spf.edit().putInt("stageNumber", 0).apply()

        spf.edit().putInt("settingAlarmColor", Color.parseColor("#000000")).apply()
        spf.edit().putInt("settingInvisibleColor", Color.parseColor("#000000")).apply()
        spf.edit().putInt("settingEnglishColor", Color.parseColor("#000000")).apply()
        spf.edit().putInt("settingRandomColor", Color.parseColor("#000000")).apply()
        spf.edit().putInt("settingCorrectColor", Color.parseColor("#000000")).apply()

        // 키보드 폰트색 복구
        spf.edit().putInt("keyboardFontColor", spf.getInt("tempKeyboardFontColor", 0)).apply()
    }


}
package com.example.friendlykeyboard.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.friendlykeyboard.R

class ChattingRVAdapter(private val chatList: ArrayList<Array<Any>>) :
    RecyclerView.Adapter<ChattingRVAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 채팅문자가 입력된 텍스트뷰 가져오기
        // 아이템 뷰를 저장하는 뷰홀더
        val textView: TextView
        val dateTextView: TextView

        init {
            textView = view.findViewById(R.id.tvChat) //  채팅문자가 입력될 텍스트뷰 찾기
            dateTextView = view.findViewById(R.id.tvChatDate)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position][0].equals(MY_CHAT))
            MY_CHAT // 내 메시지
        else
            OTHER_CHAT // 내 메시지가 아님
    }

    // 뷰홀더를 생성(레이아웃 생성)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // 아이템들을 어떻게 보여줄 것인가
        var view: View = LayoutInflater.from(viewGroup.context) // 상대방 메시지이면
            .inflate(R.layout.item_left_chat, viewGroup, false) // 말풍선이 왼쪽에서 나타나는 xml 파일 인플레이션
        if (viewType == MY_CHAT) { // 내 메시지이면
            view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_right_chat, viewGroup, false) // 말풍선이 오른쪽에서 나타나는 xml 파일 인플레이션
        }
        return ViewHolder(view)
    }

    // 뷰홀더가 재활용될 때 실행
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = chatList[position][1].toString()
        holder.dateTextView.text = chatList[position][2].toString()
    }

    override fun getItemCount(): Int = chatList.size

    companion object {
        private const val MY_CHAT = 1
        private const val OTHER_CHAT = 2
    }
}
package com.example.friendlykeyboard.keyboard

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.core.view.children
import com.example.friendlykeyboard.R

class CandidateView(context: Context, layoutInflater: LayoutInflater, textSize: Int, colorBackground: String, colorText: String) : View(context) {

    private var mCandidateView : View
    private var mCandidateItem : View
    private var mCandidateHSV : HorizontalScrollView
    private var mCandidateLL : LinearLayout
    private var mSuggestion = HashMap<String, ArrayList<String>>()
    private var layoutInflater = layoutInflater

    init{
        loadDic()

        //layout 객체 생성
        mCandidateView = layoutInflater.inflate(R.layout.keyboard_candidate_view, null)
        mCandidateItem = layoutInflater.inflate(R.layout.keyboard_candidate_item, null)

        //부모 : horizontalview
        //자식 : linearlayout --> 대체어 버튼 담을 공간 (horizontalview는 자식 뷰 하나만 가능하기 때문)
        mCandidateHSV = mCandidateView.findViewById(R.id.candidate_horizontal_view)
        mCandidateLL = mCandidateView.findViewById(R.id.candidate_linear_layout)

        mCandidateHSV.setBackgroundColor(Color.parseColor(colorBackground))

    }

    //대체어 UI 생성
    fun createView(text: String){
        if (mSuggestion.containsKey(text)){
            for(i in 0 until mSuggestion[text]!!.size){

                mCandidateItem = layoutInflater.inflate(R.layout.keyboard_candidate_item, null)
                val child = mCandidateItem.findViewById<Button>(R.id.candidate_word)
                child.text = mSuggestion[text]!!.get(i)

                mCandidateLL.addView(child)

            }
        }
        else{
            mCandidateLL.removeAllViews()
        }

    }

    fun loadDic(){
        mSuggestion.put("ㅁㅊ", arrayListOf<String>("와", "대박", "헐"))
        mSuggestion.put("ㅅㄲ야", arrayListOf<String>("친구야", "자식아", "얘야"))
        mSuggestion.put("ㅈㄴ", arrayListOf<String>("매우", "정말", "완전"))
    }

    // 추후 설정 관련
    fun setting(textSize: Int, colorBackground: String, colorText: Int){
    }


    fun getCandidate() : HorizontalScrollView{
        return mCandidateHSV
    }

}
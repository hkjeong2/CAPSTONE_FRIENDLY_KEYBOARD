package com.example.friendlykeyboard.keyboard

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.children
import com.example.friendlykeyboard.R
import com.example.friendlykeyboard.keyboard.keyboardview.KeyboardKorean

class CandidateView(context: Context, layoutInflater: LayoutInflater) : View(context) {

    private var mCandidateView : View
    private var mCandidateItem : View
    private var mCandidateHSV : HorizontalScrollView
    private var mCandidateLL : LinearLayout
    private var mSuggestion = HashMap<String, ArrayList<String>>()
    private var layoutInflater = layoutInflater
    private lateinit var mColorBackground : String
    private lateinit var mColorText : String
    private var mTextSize : Float = 0.0f

    init{
        loadDic()

        //layout 객체 생성
        mCandidateView = layoutInflater.inflate(R.layout.keyboard_candidate_view, null)
        mCandidateItem = layoutInflater.inflate(R.layout.keyboard_candidate_item, null)

        //부모 : horizontalview
        //자식 : linearlayout --> 대체어 버튼 담을 공간 (horizontalview는 자식 뷰 하나만 가능하기 때문)
        mCandidateHSV = mCandidateView.findViewById(R.id.candidate_horizontal_view)
        mCandidateLL = mCandidateView.findViewById(R.id.candidate_linear_layout)

    }

    //대체어 UI 생성
    fun createView(ic: InputConnection, text: String, stIdx: Int, edIdx: Int, keyboardKorean: KeyboardKorean) : Boolean{
        // Hashmap에 typing된 text 존재 시 UI 생성
        if (mSuggestion.containsKey(text)){
            Log.d("시험", text)
            Log.d("시험", stIdx.toString())
            Log.d("시험", edIdx.toString())
            for(i in 0 until mSuggestion[text]!!.size){
                //대체어 UI 생성
                generateCandidates(i, text, ic, stIdx, edIdx, keyboardKorean)
            }
            return true
        }
        return false
    }

    fun generateCandidates(i: Int, text: String, ic: InputConnection, stIdx: Int, edIdx: Int, keyboardKorean: KeyboardKorean){
        mCandidateItem = layoutInflater.inflate(R.layout.keyboard_candidate_item, null)
        val child = mCandidateItem.findViewById<Button>(R.id.candidate_word)
        child.text = mSuggestion[text]!!.get(i)
        child.setTextColor(Color.parseColor(mColorText))
        child.setTextSize(2, mTextSize)
        child.setOnClickListener(getMyCandidateClickListener(ic, child, stIdx, edIdx, keyboardKorean))

        mCandidateLL.addView(child)
    }

    // 추후 설정 관련
    fun setting(textSize: Float, colorBackground: String, colorText: String){
        mColorBackground = colorBackground
        mColorText = colorText
        mTextSize = textSize
        mCandidateHSV.setBackgroundColor(Color.parseColor(mColorBackground))
    }

    fun getMyCandidateClickListener(ic: InputConnection, child: Button, stIdx: Int, edIdx: Int, keyboardKorean: KeyboardKorean): OnClickListener{
        return OnClickListener {
            ic.finishComposingText()
            ic.setSelection(stIdx, edIdx)
            ic.commitText(child.text, 1)

            //대체어 변환 할 시 타이핑 중이던 한글 내용 초기화
            keyboardKorean.hangulMaker.clear()
            keyboardKorean.hangulMaker.set(0)
        }
    }

    fun eraseViews(){
        mCandidateLL.removeAllViews()
    }

    fun getCandidate() : HorizontalScrollView{
        return mCandidateHSV
    }

    //데이터셋 완료되면 수정필요
    fun loadDic(){
        mSuggestion.put("ㅁㅊ", arrayListOf<String>("와", "대박", "헐"))
        mSuggestion.put("ㅅㅂ", arrayListOf<String>("화나", "이런", "젠장"))
        mSuggestion.put("ㅅㄲ야", arrayListOf<String>("친구야", "자식아", "얘야"))
        mSuggestion.put("ㅈㄴ", arrayListOf<String>("매우", "정말", "완전"))
        mSuggestion.put("ㅎㅇ", arrayListOf<String>("안녕", "안녕하세요", "만나서 반가워"))
        mSuggestion.put("콘텐츠", arrayListOf<String>("제작물"))
        mSuggestion.put("오리지널 콘텐츠", arrayListOf<String>("자체 제작물"))
        mSuggestion.put("멀티데믹", arrayListOf<String>("감염병 복합 유행"))
        mSuggestion.put("노마드 워커", arrayListOf<String>("유목민형 노동자"))
        mSuggestion.put("디지털 트윈", arrayListOf<String>("가상 모형"))
        mSuggestion.put("커리어 하이", arrayListOf<String>("최고 기록"))
        mSuggestion.put("주스 주스", arrayListOf<String>("쥬시쿨"))
        mSuggestion.put("language", arrayListOf<String>("korean", "english", "japanese", "chinese"))
    }

}
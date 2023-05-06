package com.example.friendlykeyboard.keyboard

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
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

    //데이터셋
    private fun loadDic(){
        mSuggestion.put("18넘", arrayListOf<String>("와", "대박", "헐"))
        mSuggestion.put("10새끼", arrayListOf<String>("나쁜","그인간"))
        mSuggestion.put("10알", arrayListOf<String>("이런","망할"))
        mSuggestion.put("18것", arrayListOf<String>("멍청이"))
        mSuggestion.put("18넘", arrayListOf<String>("야","안 좋은 놈","멍청아"))
        mSuggestion.put("18년", arrayListOf<String>("야","안 좋은 놈","멍청아"))
        mSuggestion.put("18놈", arrayListOf<String>("야","안 좋은 놈","멍청아"))
        mSuggestion.put("18뇬", arrayListOf<String>("야","안 좋은 놈","멍청아"))
        mSuggestion.put("18럼", arrayListOf<String>("야","안 좋은 놈","멍청아"))
        mSuggestion.put("18롬", arrayListOf<String>("야","안 좋은 놈","멍청아"))
        mSuggestion.put("18새끼", arrayListOf<String>("야","안 좋은 놈","멍청아"))
        mSuggestion.put("18세끼", arrayListOf<String>("야","안 좋은 놈","멍청아"))
        mSuggestion.put("18섹", arrayListOf<String>("야","안 좋은 놈","멍청아"))
        mSuggestion.put("18쉑", arrayListOf<String>("야","안 좋은 놈","멍청아"))
        mSuggestion.put("18아", arrayListOf<String>("야","안 좋은 놈","멍청아"))
        mSuggestion.put("bitch", arrayListOf<String>("이런","망할","짜증"))
        mSuggestion.put("damm", arrayListOf<String>("이런","망할","짜증"))
        mSuggestion.put("fuck", arrayListOf<String>("이런","망할","짜증"))
        mSuggestion.put("g랄", arrayListOf<String>("뭐야","왜저래","헛소리"))
        mSuggestion.put("jot", arrayListOf<String>("그거"))
        mSuggestion.put("shit", arrayListOf<String>("이런","망할","짜증"))
        mSuggestion.put("shutup", arrayListOf<String>("조용","시끄러"))
        mSuggestion.put("sibal", arrayListOf<String>("이런","망할","짜증"))
        mSuggestion.put("si발", arrayListOf<String>("이런","망할","짜증"))
        mSuggestion.put("ssiba", arrayListOf<String>("이런"))
        mSuggestion.put("tlqkf", arrayListOf<String>("이런"))
        mSuggestion.put("wtf", arrayListOf<String>("이런"))
        mSuggestion.put("z랄", arrayListOf<String>("뭐야","왜저래","헛소리"))
        mSuggestion.put("ㄲㅈ", arrayListOf<String>("가","저리가","시끄러"))
        mSuggestion.put("ㄷㅊ", arrayListOf<String>("조용","시끄러"))
        mSuggestion.put("ㅁㅊ", arrayListOf<String>("망할","헐","이런"))
        mSuggestion.put("ㅂㅅ", arrayListOf<String>("바보","정신이상자","멍청이"))
        mSuggestion.put("ㅄ", arrayListOf<String>("바보","정신이상자","멍청이"))
        mSuggestion.put("ㅅㄲ야", arrayListOf<String>("친구야", "자식아", "얘야"))
        mSuggestion.put("ㅈㄴ", arrayListOf<String>("매우", "정말", "완전"))
        mSuggestion.put("ㅅㅂ", arrayListOf<String>("이런","망할"))
        mSuggestion.put("ㅅㅂㄹㅁ", arrayListOf<String>("야","안 좋은 놈","멍청아"))
        mSuggestion.put("ㅅㅍ", arrayListOf<String>("이런","망할","짜증"))
        mSuggestion.put("ㅆㅂㄹㅁ", arrayListOf<String>("야","안 좋은 놈","멍청아"))
        mSuggestion.put("ㅆㅍ", arrayListOf<String>("이런","망할"))
        mSuggestion.put("ㅈㄹ", arrayListOf<String>("뭐야","그만","이런","망할"))
        mSuggestion.put("ㅗ", arrayListOf<String>("짜증나","뭐야","이런","망할"))
        mSuggestion.put("凸", arrayListOf<String>("짜증나","뭐야","이런","망할"))
        mSuggestion.put("갈구다", arrayListOf<String>("괴롭히다","못살게 굴다"))
        mSuggestion.put("개같네", arrayListOf<String>("짜증나","뭐야","이런","망할"))
        mSuggestion.put("개같은", arrayListOf<String>("짜증나","뭐야","이런","망할"))
        mSuggestion.put("개구라", arrayListOf<String>("거짓말","헛소리"))
        mSuggestion.put("개년", arrayListOf<String>("걔","나쁜","그인간"))
        mSuggestion.put("개놈", arrayListOf<String>("걔","나쁜","그인간"))
        mSuggestion.put("개뇬", arrayListOf<String>("걔","나쁜","그인간"))
        mSuggestion.put("개뻥", arrayListOf<String>("거짓말","헛소리"))
        mSuggestion.put("개뿔", arrayListOf<String>("거짓말","헛소리"))
        mSuggestion.put("개새끼", arrayListOf<String>("걔","나쁜","그인간"))
        mSuggestion.put("개색기", arrayListOf<String>("걔","나쁜","그인간"))
        mSuggestion.put("개색끼", arrayListOf<String>("걔","나쁜","그인간"))
        mSuggestion.put("개색키", arrayListOf<String>("걔","나쁜","그인간"))
        mSuggestion.put("개색히", arrayListOf<String>("걔","나쁜","그인간"))
        mSuggestion.put("개세끼", arrayListOf<String>("걔","나쁜","그인간"))
        mSuggestion.put("개소리", arrayListOf<String>("거짓말","헛소리"))
        mSuggestion.put("개수작", arrayListOf<String>("나쁜짓","허튼짓","이상한짓"))
        mSuggestion.put("개자식", arrayListOf<String>("걔","나쁜","그인간"))
        mSuggestion.put("개허접", arrayListOf<String>("완전허접"))
        mSuggestion.put("구라", arrayListOf<String>("거짓말","헛소리"))
        mSuggestion.put("그년", arrayListOf<String>("걔","나쁜","그인간"))
        mSuggestion.put("그새끼", arrayListOf<String>("걔","나쁜","그인간"))
        mSuggestion.put("꺼져", arrayListOf<String>("가","저리가","시끄러"))
        mSuggestion.put("껃져", arrayListOf<String>("가","저리가","시끄러"))
        mSuggestion.put("꼰지르다", arrayListOf<String>("고자질하다","일러바치다"))
        mSuggestion.put("눈깔", arrayListOf<String>("눈알"))
        mSuggestion.put("눈깔어", arrayListOf<String>("뭘봐","그만봐"))
        mSuggestion.put("늬미럴", arrayListOf<String>("이런","망할","짜증"))
        mSuggestion.put("니미랄", arrayListOf<String>("이런","망할","짜증"))
        mSuggestion.put("니미럴", arrayListOf<String>("이런","망할","짜증"))
        mSuggestion.put("닥쳐", arrayListOf<String>("조용","시끄러"))
        mSuggestion.put("대가리", arrayListOf<String>("머리"))
        mSuggestion.put("되질래", arrayListOf<String>("죽을래"))
        mSuggestion.put("뒈진다", arrayListOf<String>("죽는다"))
        mSuggestion.put("뒤질래", arrayListOf<String>("죽을래"))
        mSuggestion.put("등신", arrayListOf<String>("바보","정신이상자","멍청이"))
        mSuggestion.put("디진다", arrayListOf<String>("죽는다"))
        mSuggestion.put("디질래", arrayListOf<String>("죽을래"))
        mSuggestion.put("미친", arrayListOf<String>("이런","망할","짜증"))
        mSuggestion.put("미친년", arrayListOf<String>("바보","정신이상자","멍청이"))
        mSuggestion.put("미친놈", arrayListOf<String>("바보","정신이상자","멍청이"))
        mSuggestion.put("미친새끼", arrayListOf<String>("바보","정신이상자","멍청이"))
        mSuggestion.put("병신", arrayListOf<String>("바보","정신이상자","멍청이"))
        mSuggestion.put("병자", arrayListOf<String>("바보","정신이상자","멍청이"))
        mSuggestion.put("빡치다", arrayListOf<String>("화나다"))
        mSuggestion.put("새꺄", arrayListOf<String>("야","안 좋은 놈","멍청아"))
        mSuggestion.put("샫업", arrayListOf<String>("조용","시끄러"))
        mSuggestion.put("샷업", arrayListOf<String>("조용","시끄러"))
        mSuggestion.put("시이발", arrayListOf<String>("뭐야","이런","망할","짜증"))
        mSuggestion.put("십8", arrayListOf<String>("뭐야","이런","망할","짜증"),)
        mSuggestion.put("십새끼", arrayListOf<String>("바보","정신이상자","멍청이"))
        mSuggestion.put("씨발", arrayListOf<String>("뭐야","이런","망할","짜증"))
        mSuggestion.put("시발", arrayListOf<String>("뭐야","이런","망할","짜증"))
        mSuggestion.put("씹새끼", arrayListOf<String>("바보","정신이상자","멍청이"))
        mSuggestion.put("아가리", arrayListOf<String>("입"))
        mSuggestion.put("아구창", arrayListOf<String>("입"))
        mSuggestion.put("에미", arrayListOf<String>("엄마"))
        mSuggestion.put("에바", arrayListOf<String>("지나치다","과장되다"))
        mSuggestion.put("에비", arrayListOf<String>("아빠"))
        mSuggestion.put("엿같", arrayListOf<String>("이런","망할","짜증"))
        mSuggestion.put("이년", arrayListOf<String>("야","안 좋은 놈","멍청아"))
        mSuggestion.put("이새끼", arrayListOf<String>("바보","정신이상자","멍청이"))
        mSuggestion.put("이새키", arrayListOf<String>("바보","정신이상자","멍청이"))
        mSuggestion.put("저년", arrayListOf<String>("야","안 좋은 놈","멍청아"))
        mSuggestion.put("저새끼", arrayListOf<String>("바보","정신이상자","멍청이"))
        mSuggestion.put("조까", arrayListOf<String>("짜증나","뭐야","이런","망할"))
        mSuggestion.put("존나", arrayListOf<String>("정말","매우","엄청","완전"))
        mSuggestion.put("졸라", arrayListOf<String>("정말","매우","엄청","완전"))
        mSuggestion.put("좁밥", arrayListOf<String>("허접"))
        mSuggestion.put("좆같", arrayListOf<String>("짜증나","뭐야","이런","망할"))
        mSuggestion.put("좆까", arrayListOf<String>("짜증나","뭐야","이런","망할"))
        mSuggestion.put("좆나", arrayListOf<String>("매우","엄청","완전"))
        mSuggestion.put("지랄", arrayListOf<String>("뭐야","왜저래","헛소리"))
        mSuggestion.put("짱", arrayListOf<String>("최고","굉장하다"))
        mSuggestion.put("쩐다", arrayListOf<String>("대단해","멋져"))
        mSuggestion.put("쩔어", arrayListOf<String>("대단해","멋져"))
        mSuggestion.put("쪼개다", arrayListOf<String>("실실 웃다","웃다"))
        mSuggestion.put("쪽팔", arrayListOf<String>("창피","망신"))
        mSuggestion.put("창남", arrayListOf<String>("바람둥이","파렴치한"))
        mSuggestion.put("창녀", arrayListOf<String>("성매매 여성","파렴치한"))
        mSuggestion.put("창년", arrayListOf<String>("성매매 여성","파렴치한"))
        mSuggestion.put("퍽큐", arrayListOf<String>("바보","정신이상자","멍청이"))
        mSuggestion.put("호구", arrayListOf<String>("속은 사람","바보","멍청이"))
        mSuggestion.put("후래자식", arrayListOf<String>("싸가지 없는","말이 거친"))
        mSuggestion.put("후레자식", arrayListOf<String>("싸가지 없는","말이 거친"))

        mSuggestion.put("콘텐츠", arrayListOf<String>("제작물"))
        mSuggestion.put("오리지널 콘텐츠", arrayListOf<String>("자체 제작물"))
        mSuggestion.put("멀티데믹", arrayListOf<String>("감염병 복합 유행"))
        mSuggestion.put("노마드 워커", arrayListOf<String>("유목민형 노동자"))
        mSuggestion.put("디지털 트윈", arrayListOf<String>("가상 모형"))
        mSuggestion.put("커리어 하이", arrayListOf<String>("최고 기록"))
    }

}
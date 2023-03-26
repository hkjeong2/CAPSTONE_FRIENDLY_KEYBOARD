package com.example.friendlykeyboard.keyboard

import android.graphics.Color
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import com.example.friendlykeyboard.R
import com.example.friendlykeyboard.keyboard.keyboardview.*

class KeyBoardService : InputMethodService(){
    lateinit var keyboardView:LinearLayout
    lateinit var keyboardFrame:FrameLayout
    lateinit var keyboardKorean:KeyboardKorean
    lateinit var keyboardEnglish:KeyboardEnglish
    lateinit var keyboardSymbols:KeyboardSymbols
    lateinit var mCandidateView: CandidateView
    var isQwerty = 0 // shared preference에 데이터를 저장하고 불러오는 기능 필요

    val keyboardInterationListener = object:KeyboardInteractionListener{
        //inputconnection이 null일경우 재요청하는 부분 필요함
        override fun modechange(mode: Int) {
            //작성 중인 텍스트를 commit
            currentInputConnection.finishComposingText()

            //mode 별로 keyboard frame 변환
            when(mode){
                0 ->{
                    keyboardFrame.removeAllViews()
                    keyboardEnglish.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardEnglish.getLayout())
                }
                1 -> {
                    if(isQwerty == 0){
                        keyboardFrame.removeAllViews()
                        keyboardKorean.inputConnection = currentInputConnection
                        keyboardFrame.addView(keyboardKorean.getLayout())
                    }
                    else{
                        keyboardFrame.removeAllViews()
                        keyboardFrame.addView(KeyboardChunjiin.newInstance(applicationContext, layoutInflater, currentInputConnection, this))
                    }
                }
                2 -> {
                    keyboardFrame.removeAllViews()
                    keyboardSymbols.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardSymbols.getLayout())
                }
                3 -> {
                    keyboardFrame.removeAllViews()
                    keyboardFrame.addView(KeyboardEmoji.newInstance(applicationContext, layoutInflater, currentInputConnection, this))
                }
            }
        }

        override fun sendText(text: String) {
            //text field에 typing된 text를 AI모델로 전송 작업
            //type될 때마다 call back
            Log.d("시험 : ", text)
            mCandidateView.createView(text)
        }
    }

    override fun onCreate() {
        super.onCreate()
        //keyboard가 될 전체 레이아웃과 입력방식에 따라 다르게 채워질 framelayout 정의
        keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null) as LinearLayout
        keyboardFrame = keyboardView.findViewById(R.id.keyboard_frame)
    }

    override fun onCreateInputView(): View {
        //각 type의 keyboard 생성
        keyboardKorean = KeyboardKorean(applicationContext, layoutInflater, keyboardInterationListener)
        keyboardEnglish = KeyboardEnglish(applicationContext, layoutInflater, keyboardInterationListener)
        keyboardSymbols = KeyboardSymbols(applicationContext, layoutInflater, keyboardInterationListener)
        keyboardKorean.inputConnection = currentInputConnection
        keyboardKorean.init()
        keyboardEnglish.inputConnection = currentInputConnection
        keyboardEnglish.init()
        keyboardSymbols.inputConnection = currentInputConnection
        keyboardSymbols.init()

        setCandidatesViewShown(true);
        //EditText에 포커스가 갈 경우 호출되는 View
        return keyboardView
    }


    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        //focus onto text field --> keyboard 올라올 때
        Log.d("IMEstart", "1")
    }

    override fun onFinishInput() {
        super.onFinishInput()
        //focus out of text field --> keyboard 내려갈 때
       Log.d("IMEfinish", "0")
    }

    override fun onUpdateSelection(
        oldSelStart: Int, oldSelEnd: Int,
        newSelStart: Int, newSelEnd: Int,
        candidatesStart: Int, candidatesEnd: Int
    ) {
        super.onUpdateSelection(
            oldSelStart, oldSelEnd,
            newSelStart, newSelEnd,
            candidatesStart, candidatesEnd
        )
        //text field 내에서 사용자 클릭에 의해 커서가 변경될 때마다,
        //text 추가될 때마다 call back
        Log.d("IMEupdateindex olds", oldSelStart.toString())
        Log.d("IMEupdateindex olde", oldSelEnd.toString())
        Log.d("IMEupdateindex news", newSelStart.toString())
        Log.d("IMEupdateindex newe", newSelEnd.toString())
    }



    override fun updateInputViewShown() {
        //현재 필요한 키보드를 결정하고 수정
        super.updateInputViewShown()
        currentInputConnection.finishComposingText()
        //숫자 입력 editText일 시 Numpad로 변환
        if(currentInputEditorInfo.inputType == EditorInfo.TYPE_CLASS_NUMBER){
            keyboardFrame.removeAllViews()
            keyboardFrame.addView(KeyboardNumpad.newInstance(applicationContext, layoutInflater, currentInputConnection, keyboardInterationListener))
        }
        else{
            keyboardInterationListener.modechange(1)
        }
    }


    //화면이 위로 스크롤 되면서 candidate view도 자리를 차지
    override fun onComputeInsets(outInsets: Insets?) {
        super.onComputeInsets(outInsets)
        if (!isFullscreenMode()) {
            outInsets?.contentTopInsets = outInsets?.visibleTopInsets
        }
    }

    override fun onCreateCandidatesView(): View {
        //sharedPreference로 textSize, colorBackground, colorText 지정 가능
        //키보드 생성시 함께 1회만 생성되는 듯 (동적으로 바꾸고자 할 시 다른 곳에서 mCandidateView 수정해주면 어떨지?)
        mCandidateView = CandidateView(this, layoutInflater)
        mCandidateView.setting(12.0f, "#dddddd", "#ffffff")
        return mCandidateView.getCandidate()

    }



}
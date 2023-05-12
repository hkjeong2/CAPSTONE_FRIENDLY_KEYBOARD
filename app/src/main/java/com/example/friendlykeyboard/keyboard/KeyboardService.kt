package com.example.friendlykeyboard.keyboard

import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.friendlykeyboard.ChattingActivity
import com.example.friendlykeyboard.MainActivity
import com.example.friendlykeyboard.R
import com.example.friendlykeyboard.keyboard.keyboardview.*
import com.example.friendlykeyboard.retrofit_util.HateSpeech
import com.example.friendlykeyboard.retrofit_util.RetrofitClient
import kotlinx.coroutines.*

class KeyBoardService : InputMethodService() {
    private lateinit var pref: SharedPreferences
    lateinit var keyboardView:LinearLayout
    lateinit var keyboardFrame:FrameLayout
    lateinit var keyboardKorean:KeyboardKorean
    lateinit var keyboardEnglish:KeyboardEnglish
    lateinit var keyboardSymbols:KeyboardSymbols
    lateinit var mCandidateView: CandidateView
    lateinit var notificationManager : NotificationManager
    var keyboardMode = -1 //keyboard 종류
    var idx = 0 //candidateView에 필요
    var isQwerty = 0 // shared preference에 데이터를 저장하고 불러오는 기능 필요
    var count = 0
    var stage = 0 //2단계 제재 중 특정 기능
    private val service = RetrofitClient.getApiService()

    val keyboardInterationListener = object:KeyboardInteractionListener{
        //inputconnection이 null일경우 재요청하는 부분 필요함
        override fun modechange(mode: Int) {
            //작성 중인 텍스트를 commit
            currentInputConnection.finishComposingText()

            //mode 별로 keyboard frame 변환
            when(mode){
                0 ->{
                    keyboardMode = 0
                    keyboardFrame.removeAllViews()
                    keyboardEnglish.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardEnglish.getLayout())
                }
                1 -> {
                    keyboardMode = 1
                    if(isQwerty == 0){
                        keyboardFrame.removeAllViews()
                        keyboardKorean.inputConnection = currentInputConnection
                        keyboardFrame.addView(keyboardKorean.getLayout(0))
                    }
                    else{
                        keyboardFrame.removeAllViews()
                        keyboardFrame.addView(KeyboardChunjiin.newInstance(applicationContext, layoutInflater, currentInputConnection, this))
                    }
                }
                2 -> {
                    keyboardMode = 2
                    keyboardFrame.removeAllViews()
                    keyboardSymbols.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardSymbols.getLayout())
                }
                3 -> {
                    keyboardMode = 3
                    keyboardFrame.removeAllViews()
                    keyboardFrame.addView(KeyboardEmoji.newInstance(applicationContext, layoutInflater, currentInputConnection, this))
                }
            }
        }

        //text type될 때마다 call back
        //후보뷰 삭제 및 생성
        override fun sendText(text: String) {
            updateCandidates(text)
        }

        //Enter키로 전송된 text AI로 검사
        override fun checkText(text: String) {
            lateinit var response : String
            runBlocking{
                response = checkTexts(text)
            }
            checkResponse(response)
        }
    }

    private fun checkResponse(response : String){
        // 오류 케이스
        if (response == "")
            return

        // 무작위 배치 단계의 제재 중일 시 typing 마다 계속 shuffle
        if (stage == 4 && keyboardMode == 1){
            keyboardKorean.shuffleKeyboard()
            keyboardInterationListener.modechange(1)
        }

        // 10개의 Labels
        // "여성/가족", "남성", "성소수자", "인종/국적", "연령"
        // "지역", "종교", "기타 혐오", "악플/욕설", "clean"
        when (response) {
            "clean" -> {
            }
            else -> {
                count++
                checkCount(response)
            }
        }
    }

    private suspend fun checkTexts(text : String) : String {
        // 서버에서 혐오 표현 존재 여부를 판별함.
        val id = getSharedPreferences("cbAuto", 0).getString("id", "")!!
        val hateSpeech = HateSpeech(id, text)
        lateinit var result : String

        withContext(CoroutineScope(Dispatchers.IO).coroutineContext){
            try {
                val response = service.inferenceHateSpeech(hateSpeech)

                if (response.isSuccessful) {
                    result = response.body()?.inference_hate_speech_result!!

                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
                    }

                } else {
                    // 통신이 실패한 경우
                    result = ""
                    Log.d("KeyboardService", response.message())
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(applicationContext,"오류가 발생하였습니다.",Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                result = ""
                Log.d("KeyboardService", "Connection Error")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(applicationContext,"서버와의 통신이 실패하였습니다.",Toast.LENGTH_SHORT).show()
                }
            }
        }

        return result
    }

    // count 횟수에 따른 3단계 기능 적용
    private fun checkCount(text: String) {
        if (count >= 2){
            stage++
            pref.edit().putInt("stageNumber", stage).apply()
            // 단계 변경에 따른 UI 변경
            changeUI()
            // 단계 변경에 따른 이전 단계 교정 기능 해제
            releasePreviousMode()
            // 변경된 단계 실행
            implementStage(text)
            if (stage >= 2){
                // 키보드 복구를 위한 수행 과제 알림
                notifyChance()
            }
            count = 0
        }

        //아래 두 기능은 나머지와 달리 계속 실행해주는 기능
        when (stage){
            1 -> {
                pushAlarm(text)
            }
            5 -> {
                textMasking()
            }
        }

    }

    private fun changeUI(){
        val spf : SharedPreferences = getSharedPreferences("setting", 0)
        when (stage){
            2 -> {
                spf.edit().putInt("settingAlarmColor", Color.parseColor("#80000000")).apply()
            }
            3 -> {
                spf.edit().putInt("settingInvisibleColor", Color.parseColor("#80000000")).apply()
            }
            4 -> {
                spf.edit().putInt("settingEnglishColor", Color.parseColor("#80000000")).apply()
            }
            5 -> {
                spf.edit().putInt("settingRandomColor", Color.parseColor("#80000000")).apply()
                spf.edit().putInt("settingCorrectColor", Color.parseColor("#80000000")).apply()
            }
        }
    }

    private fun releasePreviousMode(){
        when (stage){
            3 -> {
                //2단계 모드 해제 (투명 모드)
                pref.edit().putInt("keyboardFontColor", pref.getInt("tempKeyboardFontColor", 0)).apply()
                keyboardKorean.updateKeyboard()
                keyboardEnglish.updateKeyboard()
            }
            4 -> {
                //3단계 모드 해제 (영어 모드)
                keyboardEnglish.setChangingModeAvailability(true)
            }
            5 -> {
                //4단계 모드 해제 (무작위 모드)
                keyboardKorean.restoreKeyboard()
                if (keyboardMode == 1){
                    // 한글 키보드를 사용중이었다면 복구된 한글 키보드로 교체
                    keyboardFrame.removeAllViews()
                    keyboardKorean.inputConnection = currentInputConnection
                    // 기존에는 키보드 타입 전환 시 call 되던 getLayout이 HangulMaker를 무조건 새 객체로 초기화
                    // --> 작성 중이던 한글을 저장할 필요가 없었기 때문
                    // 하지만 일정 시간 지난 뒤 기본 한글 키보드로 교체하는 여기 part에서는 위 경우를 구분해줘야함
                    // --> 이전에 한글을 작성중이었을 수 있기 때문 --> getLayout(1 or 0)으로 구분
                    keyboardFrame.addView(keyboardKorean.getLayout(1))
                }
            }
        }
    }

    private fun implementStage(text: String){
        when (stage){
            1 -> {
                pushAlarm(text)
            }
            2 -> {
                invisibleKeyboard()
            }
            3 -> {
                allowEngKeyboardOnly()
            }
            4 -> {
                shuffleKeyboard()
                keyboardInterationListener.modechange(1)
            }
            5 -> {
                textMasking()
            }
            6 -> {
                stage--
                pref.edit().putInt("stageNumber", stage).apply()
            }
        }
    }

    private fun notifyChance(){
        val intent = Intent(this, ChattingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val text = "키보드를 복구하기 위해 미션을 수행해 주세요!"

        createNotification(text, R.drawable.tasks, 2, intent)
    }

    // 일정 횟수 이상 비속어 사용 시 푸시 알림 생성
    private fun pushAlarm(curse : String) {
        //받아온 비속어로 text 수정
        val text = curse + " 혐오적인 말을 자제해 주세요!"

        // 알림 눌렀을 때 이동할 intent 구분 필요
        // 1) 로그인 된 상태면 MainActivit
        // 2) 로그인되지 않은 상태면 LoginActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            //현재 액티비티에서 새로운 액티비티를 실행한다면 현재 액티비티를 새로운 액티비티로 교체하는 플래그
            //flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
            //이전에 실행된 액티비티들을 모두 없엔 후 새로운 액티비티 실행 플래그
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        createNotification(text, R.drawable.swearing, 1, intent)
    }

    private fun createNotification(text : String, image : Int, notificationID : Int, intent : Intent){
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this,0,intent,FLAG_IMMUTABLE)

        //노티피케이션 생성
        val notification: Notification = NotificationCompat.Builder(this, "channelID")
            .setContentTitle("FriendlyKeyboard")
            .setContentText(text)
            .setSmallIcon(image) //아이콘이미지
            .setAutoCancel(true) // 사용자가 알림을 탭하면 자동으로 알림을 삭제합니다.
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) //노티클릭시 인텐트작업
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .build()

        notificationManager.notify(notificationID, notification)
    }

    /* 2. 채널 만들기 및 중요도 설정*/
    private fun createNotificationChannel(id: String, name: String, channelDescription: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //중요도
            val importance = NotificationManager.IMPORTANCE_HIGH
            //채널 생성
            val channel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    // 키보드 폰트 글자가 보이지 않도록 하는 기능
    private fun invisibleKeyboard() {
        Toast.makeText(applicationContext, "교정 : 투명 모드", Toast.LENGTH_SHORT).show()
        val fontColor = pref.getInt("keyboardFontColor", 0)
        pref.edit().putInt("tempKeyboardFontColor", fontColor).apply()
        val keyboardColor = pref.getInt("keyboardColor", 0)

        pref.edit().putInt("keyboardFontColor", keyboardColor).apply()
        keyboardKorean.updateKeyboard()
        keyboardEnglish.updateKeyboard()

//        // 일정 시간 뒤 다시 폰트 글자가 보이도록 수정
//        GlobalScope.launch(Dispatchers.Main) {
//            delay(delayTime)
//            pref.edit().putInt("keyboardFontColor", fontColor).apply()
//            keyboardKorean.updateKeyboard()
//            keyboardEnglish.updateKeyboard()
//        }
    }

    private fun allowEngKeyboardOnly(){
        Toast.makeText(applicationContext, "교정 : 영문 키보드", Toast.LENGTH_SHORT).show()
        keyboardInterationListener.modechange(0)
        //다른 키보드 모드로 바꾸지 못하도록
        keyboardEnglish.setChangingModeAvailability(false)

//        //일정 시간 뒤 모드 변경 잠금 해제
//        GlobalScope.launch(Dispatchers.Main){
//            delay(delayTime)
//            keyboardEnglish.setChangingModeAvailability(true)
//        }
    }

    private fun shuffleKeyboard(){
        Toast.makeText(applicationContext, "교정 : 키보드 무작위 배치", Toast.LENGTH_SHORT).show()
        // Enter key의 clickListener를 한 번만 연동시키기 위함
        // 무작위 배치 키보드로 즉시 변경하는 과정에서의 정확한 오류 원인이 무엇인진 모르겠으나
        // 위와 같이 하면 해결 되는 듯 함
        keyboardKorean.isFirst= false

        // keyboard 섞기
        keyboardKorean.shuffleKeyboard()

//        // coroutine delayed로 일정 시간 뒤 키보드 화면 교체
//        GlobalScope.launch(Dispatchers.Main){
//            delay(delayTime)
//            // keyboard 원상 복구
//            keyboardKorean.restoreKeyboard()
//
//            if (keyboardMode == 1){
//                // 한글 키보드를 사용중이었다면 복구된 한글 키보드로 교체
//                keyboardFrame.removeAllViews()
//                keyboardKorean.inputConnection = currentInputConnection
//                // 기존에는 키보드 타입 전환 시 call 되던 getLayout이 HangulMaker를 무조건 새 객체로 초기화
//                // --> 작성 중이던 한글을 저장할 필요가 없었기 때문
//                // 하지만 일정 시간 지난 뒤 기본 한글 키보드로 교체하는 여기 part에서는 위 경우를 구분해줘야함
//                // --> 이전에 한글을 작성중이었을 수 있기 때문 --> getLayout(1 or 0)으로 구분
//                keyboardFrame.addView(keyboardKorean.getLayout(1))
//            }
//
//            stage = -1
//        }
    }

    private fun textMasking(){
        var tempText = ""
        val text = currentInputConnection.getExtractedText(ExtractedTextRequest(), InputConnection.GET_TEXT_WITH_STYLES).text
        for (i in text.indices){
            if (text[i] == ' '){
                tempText += ' '
            }
            else{
                tempText += '*'
            }
        }
        currentInputConnection.deleteSurroundingText(1000, 1000)
        currentInputConnection.commitText(tempText, 0)
    }

    private fun initStage(){
        stage = pref.getInt("stageNumber", 0)

        when (stage){
            3 -> {
                allowEngKeyboardOnly()
            }
            4 -> {
                shuffleKeyboard()
                keyboardInterationListener.modechange(1)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        //keyboard가 될 전체 레이아웃과 입력방식에 따라 다르게 채워질 framelayout 정의
        keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null) as LinearLayout
        keyboardFrame = keyboardView.findViewById(R.id.keyboard_frame)
        pref = getSharedPreferences("setting", Activity.MODE_PRIVATE)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel("channelID", "FriendlyKeyboardChannel", "FriendlyKeyboard")

        Log.d("키보드 생성", "111")
    }

    override fun onCreateInputView(): View {
        //각 type의 keyboard 생성
        Log.d("키보드 생성2", "222")
        keyboardKorean = KeyboardKorean(applicationContext, layoutInflater, keyboardInterationListener)
        keyboardEnglish = KeyboardEnglish(applicationContext, layoutInflater, keyboardInterationListener)
        keyboardSymbols = KeyboardSymbols(applicationContext, layoutInflater, keyboardInterationListener)
        keyboardKorean.inputConnection = currentInputConnection
        keyboardKorean.init()
        keyboardEnglish.inputConnection = currentInputConnection
        keyboardEnglish.init()
        keyboardSymbols.inputConnection = currentInputConnection
        keyboardSymbols.init()

        initStage()

        updateKeyboard()

        setCandidatesViewShown(true)
        //EditText에 포커스가 갈 경우 호출되는 View
        return keyboardView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)

        // 사용자 미션 수행으로 인해 stage가 바뀐 점이 있는지 확인
        checkStageHasChanged()

        //focus onto text field --> keyboard 올라올 때
        //선택된 커서 블록에 대체어 존재 시 바로 후보뷰 생성 해줘야 함
        idx = currentInputConnection.getTextBeforeCursor(1000, 0).toString().length
        updateCandidates(currentInputConnection?.getExtractedText(ExtractedTextRequest(), InputConnection.GET_TEXT_WITH_STYLES)?.text.toString())

        Log.d("키보드 생성3", "333")
    }

    override fun onFinishInput() {
        super.onFinishInput()
        //focus out of text field --> keyboard 내려갈 때

        //후보뷰 초기화
        if (::mCandidateView.isInitialized)
            mCandidateView.eraseViews()

        Log.d("키보드 닫기", "111")
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)

        Log.d("IMEfinish", "1")
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
        //1) text field 내에서 사용자 클릭에 의해 커서가 변경될 때마다
        //2) text 추가될 때마다
        // call back
        
        //현재 커서 인덱스 저장
        idx = newSelStart

        //유저가 text field 내 text 클릭 시 해당 블록과 주변 text 검사하여 candidate view 생성 및 삭제
        if (newSelStart != candidatesEnd || newSelEnd != candidatesEnd )
            updateCandidates(currentInputConnection?.getExtractedText(ExtractedTextRequest(), InputConnection.GET_TEXT_WITH_STYLES)?.text.toString())

        Log.d("IMEupdateindex olds", oldSelStart.toString())
        Log.d("IMEupdateindex news", newSelStart.toString())
        Log.d("IMEupdateindex cs", candidatesStart.toString())
        Log.d("IMEupdateindex ce", candidatesEnd.toString())
    }

    // 키보드 속성 값을 설정하는 메소드
    private fun updateKeyboard(){
        if (::keyboardKorean.isInitialized) {
            // 키보드 크기 업데이트
            // 폰트 색깔 업데이트
            // 키보드 색상 업데이트
            // 키보드 배경색 업데이트
            keyboardKorean.updateKeyboard()
            keyboardEnglish.updateKeyboard()
            // 대체어 뷰 레이아웃 , 버튼 , 폰트 업데이트
            if (::mCandidateView.isInitialized){
                mCandidateView.updateSetting()
            }
        }
    }

    override fun updateInputViewShown() {
        updateKeyboard()

        Log.d("키보드 수정", "111")
        //현재 필요한 키보드를 결정하고 수정
        super.updateInputViewShown()
        currentInputConnection.finishComposingText()
        //숫자 입력 editText일 시 Numpad로 변환
        if(currentInputEditorInfo.inputType == EditorInfo.TYPE_CLASS_NUMBER){
            keyboardFrame.removeAllViews()
            keyboardFrame.addView(KeyboardNumpad.newInstance(applicationContext, layoutInflater, currentInputConnection, keyboardInterationListener))
        }
        else{
            // 키보드 올릴 때 영어 모드 제재 여부 확인
            if (keyboardEnglish.blockMode){
                keyboardInterationListener.modechange(0)
            }
            else{
                keyboardInterationListener.modechange(1)
            }
        }
    }

    //화면이 위로 스크롤 되면서 candidate view도 자리를 차지
    override fun onComputeInsets(outInsets: Insets?) {
        super.onComputeInsets(outInsets)
        if (!isFullscreenMode) {
            outInsets?.contentTopInsets = outInsets?.visibleTopInsets
        }
    }

    override fun onCreateCandidatesView(): View {
        //sharedPreference로 textSize, colorBackground, colorText 지정 가능
        //키보드 생성시 함께 1회만 생성되는 듯 (동적으로 바꾸고자 할 시 다른 곳에서 mCandidateView 수정해주면 어떨지?)
        mCandidateView = CandidateView(this, layoutInflater, getSharedPreferences("setting", 0))
        return mCandidateView.getCandidate()
    }

    private fun checkStageHasChanged(){
        val stageValue = pref.getInt("stageNumber", 0)
        if (stage != stageValue && stageValue == 0){
            stage++
            releasePreviousMode()
            stage = 0
            count = 0
        }
    }

    private fun updateCandidates(text : String){
        if (::mCandidateView.isInitialized){
            //새로운 문자가 들어왔으므로 이전 후보뷰 모두 삭제
            mCandidateView.eraseViews()

            // 문장을 띄어쓰기 기준으로 토큰화
            // 현재 커서를 중심으로
            // 1) 왼쪽 방향으로   2) 오른쪽 방향으로
            // 토큰들을 조합하며 대체어 존재 유무 알기위해 사전 검색 (있으면 후보뷰 생성)

            val token : List<String> = text.replace("\n", " ").split(" ")
            val tokenIdxRange = ArrayList<ArrayList<Int>>()
            //현재 커서가 위치한 토큰의 Idx 탐색
            val tokenIdx = findCursorPos(token, tokenIdxRange)

            //커서의 현재위치 기준에서 양방향으로 토큰 append하며 검색
            createCandidateView(token, tokenIdxRange, tokenIdx)

        }
    }

    private fun findCursorPos(token : List<String>, tokenIdxRange : ArrayList<ArrayList<Int>>) : Int{
        var begin = 0
        var tokenIdx = 0

        //문장의 각 어절이 가진 문자열 길이로 index 범위 저장 --> 커서의 위치가 해당 범위내 존재 시 tokenIdx 저장
        if (token.size > 0){
            for (i in token.indices){
                tokenIdxRange.add(ArrayList())
                tokenIdxRange.get(i).add(begin)
                val st = begin
                begin += token.get(i).length
                tokenIdxRange.get(i).add(begin)
                val en = begin
                begin += 1

                if (st <= idx && idx <= en){
                    tokenIdx = i
                }
            }
        }
        return tokenIdx
    }

    private fun createCandidateView(token : List<String>, tokenIdxRange: ArrayList<ArrayList<Int>>, tokenIdx: Int){
        var mText = ""
        var space = ""
        //현재 커서 기준 양방향으로 각각 for문 하나를 사용해 단어 조합을 검색하기 때문에
        //첫 문자가 겹치는 경우 발생 --> 해당 케이스 제거용 flag
        var isFirstWordAgain = false
        val st = tokenIdxRange.get(tokenIdx).get(0)
        val ed = tokenIdxRange.get(tokenIdx).get(1)

        for (i in tokenIdx downTo 0){
            if (i != tokenIdx)
                space = " "
            mText = token.get(i) + space + mText
            if (mCandidateView.createView(currentInputConnection, mText, tokenIdxRange.get(i).get(0), ed, keyboardKorean) && i == tokenIdx)
                isFirstWordAgain = true
        }

        mText = ""
        space = ""

        for (i in tokenIdx until token.size){
            //앞선 for문에서 첫 word로 대체어 생성했기 때문에 skip
            if (i == tokenIdx && isFirstWordAgain)
                continue
            if (i != tokenIdx)
                space = " "
            mText += space + token.get(i)
            mCandidateView.createView(currentInputConnection, mText, st, tokenIdxRange.get(i).get(1), keyboardKorean)
        }
    }
}
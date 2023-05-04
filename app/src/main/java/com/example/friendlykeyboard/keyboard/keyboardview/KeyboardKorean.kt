package com.example.friendlykeyboard.keyboard.keyboardview

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Typeface
import android.inputmethodservice.Keyboard
import android.media.AudioManager
import android.os.*
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.children
import com.example.friendlykeyboard.R
import com.example.friendlykeyboard.keyboard.KeyboardInteractionListener
import kotlinx.coroutines.*
import java.lang.NumberFormatException
import java.lang.Runnable

class KeyboardKorean constructor(var context:Context, var layoutInflater: LayoutInflater, var keyboardInterationListener: KeyboardInteractionListener){
    lateinit var koreanLayout: LinearLayout
    var isCaps:Boolean = false
    var isFirst = true
    var buttons:MutableList<Button> = mutableListOf<Button>()
    lateinit var hangulMaker: HangulMaker
    lateinit var vibrator: Vibrator
    lateinit var sharedPreferences:SharedPreferences
    var inputConnection:InputConnection? = null
        set(inputConnection){
            field = inputConnection
        }
    var sound = 0
    var vibrate = 0
    val numpadText = listOf<String>("1","2","3","4","5","6","7","8","9","0")
    val firstLineText = listOf<String>("ㅂ","ㅈ","ㄷ","ㄱ","ㅅ","ㅛ","ㅕ","ㅑ","ㅐ","ㅔ")
    val secondLineText = listOf<String>("ㅁ","ㄴ","ㅇ","ㄹ","ㅎ","ㅗ","ㅓ","ㅏ","ㅣ")
    val thirdLineText = listOf<String>("CAPS","ㅋ","ㅌ","ㅊ","ㅍ","ㅠ","ㅜ","ㅡ","DEL")
    val fourthLineText = listOf<String>("!#1","한/영",",","space",".","Enter")
//    val firstLongClickText = listOf("!","@","#","$","%","^","&","*","(",")")
//    val secondLongClickText = listOf<String>("~","+","-","×","♥",":",";","'","\"")
//    val thirdLongClickText = listOf("","_","<",">","/",",","?")
    val myKeysText = ArrayList<List<String>>()
//    val myLongClickKeysText = ArrayList<List<String>>()
    val layoutLines = ArrayList<LinearLayout>()
    var downView:View? = null
    var capsView:ImageView? = null
    var mode: Int = 0

    lateinit var numpadLine : LinearLayout
    lateinit var firstLine: LinearLayout
    lateinit var secondLine: LinearLayout
    lateinit var thirdLine: LinearLayout
    lateinit var fourthLine: LinearLayout

    fun shuffleKeyboard(){
        //한글 자음과 모음이 속한 line
        var shuffledNumpadText = mutableListOf<String>()
        var shuffledFirstLineText = mutableListOf<String>()
        var shuffledSecondLineText = mutableListOf<String>()
        var shuffledThirdLineText = mutableListOf<String>()

        //키보드 한글 섞기
        val hangulCharacters = mutableListOf<String>("ㅂ","ㅈ","ㄷ","ㄱ","ㅅ","ㅛ","ㅕ","ㅑ","ㅐ","ㅔ","ㅁ","ㄴ","ㅇ","ㄹ","ㅎ","ㅗ","ㅓ","ㅏ","ㅣ","ㅋ","ㅌ","ㅊ","ㅍ","ㅠ","ㅜ","ㅡ")
        hangulCharacters.shuffle()

        //키보드 숫자 섞기
        val numbers = mutableListOf<String>("0","1","2","3","4","5","6","7","8","9")
        numbers.shuffle()

        for (i in 0 .. 9){
            shuffledNumpadText.add(numbers[i])
        }
        for (i in 0 .. 9){
            shuffledFirstLineText.add(hangulCharacters[i])
        }
        for (i in 10 .. 18){
            shuffledSecondLineText.add(hangulCharacters[i])
        }
        shuffledThirdLineText.add("CAPS")
        for (i in 19 .. 25){
            shuffledThirdLineText.add(hangulCharacters[i])
        }
        shuffledThirdLineText.add("DEL")

        clearMyKeysText(shuffledNumpadText, shuffledFirstLineText, shuffledSecondLineText, shuffledThirdLineText)
    }

    fun restoreKeyboard(){
        clearMyKeysText(numpadText, firstLineText,secondLineText,thirdLineText)
    }

    fun clearMyKeysText(num:List<String>, first:List<String>, second:List<String>, third:List<String>){
        myKeysText.clear()
        myKeysText.add(num)
        myKeysText.add(first)
        myKeysText.add(second)
        myKeysText.add(third)
        myKeysText.add(fourthLineText)
    }

    // 키보드 속성 업데이트
    fun updateKeyboard(){
        val height = sharedPreferences.getInt("keyboardHeight", 150)
        val paddingLeft = sharedPreferences.getInt("keyboardPaddingLeft", 0)
        val paddingRight = sharedPreferences.getInt("keyboardPaddingRight", 0)
        val paddingBottom = sharedPreferences.getInt("keyboardPaddingBottom", 0)
        val fontColor = sharedPreferences.getInt("keyboardFontColor", 0)
        val fontStyle = sharedPreferences.getBoolean("keyboardFontStyle", false)
        val keyboardColor = sharedPreferences.getInt("keyboardColor", 0)
        val keyboardBackgroundColor = sharedPreferences.getInt("keyboardBackground", 0)

        // 키보드 padding 업데이트
        koreanLayout.setPadding(paddingLeft, 0, paddingRight, paddingBottom)

        // 키보드 높이 업데이트
        if(context.getResources().configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            numpadLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height * 0.7).toInt())
            firstLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height*0.7).toInt())
            secondLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height*0.7).toInt())
            thirdLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height*0.7).toInt())
            fourthLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height * 0.7).toInt())
        }else{
            numpadLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
            firstLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
            secondLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
            thirdLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
            fourthLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
        }
        
        // 키보드 폰트, 자판 색, 폰트 색 업데이트
        for (button in buttons) {
            if (fontStyle) {
                button.setTypeface(null, Typeface.BOLD)
            } else {
                button.setTypeface(null, Typeface.NORMAL)
            }

            button.setTextColor(fontColor)
            button.background.setTint(keyboardColor)
        }

        // 키보드 배경색 업데이트
        koreanLayout.setBackgroundColor(keyboardBackgroundColor)
    }

    fun init(){
        koreanLayout = layoutInflater.inflate(R.layout.keyboard_action, null) as LinearLayout
        hangulMaker = HangulMaker(inputConnection!!)
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE)
        sound = sharedPreferences.getInt("keyboardSound", -1)
        vibrate = sharedPreferences.getInt("keyboardVibrate", -1)

        numpadLine = koreanLayout.findViewById<LinearLayout>(
            R.id.numpad_line
        )
        firstLine = koreanLayout.findViewById<LinearLayout>(
            R.id.first_line
        )
        secondLine = koreanLayout.findViewById<LinearLayout>(
            R.id.second_line
        )
        thirdLine = koreanLayout.findViewById<LinearLayout>(
            R.id.third_line
        )
        fourthLine = koreanLayout.findViewById<LinearLayout>(
            R.id.fourth_line
        )

        updateKeyboard()

        clearMyKeysText(numpadText, firstLineText,secondLineText,thirdLineText)

//        myLongClickKeysText.clear()
//        myLongClickKeysText.add(firstLongClickText)
//        myLongClickKeysText.add(secondLongClickText)
//        myLongClickKeysText.add(thirdLongClickText)

        layoutLines.clear()
        layoutLines.add(numpadLine)
        layoutLines.add(firstLine)
        layoutLines.add(secondLine)
        layoutLines.add(thirdLine)
        layoutLines.add(fourthLine)
        setLayoutComponents()
    }

    fun getLayout(mode : Int):LinearLayout{
        if (mode != 1)  // 키보드 종류 변환 시에만 작성 중이던 한글 정보 초기화
            hangulMaker = HangulMaker(inputConnection!!)
        setLayoutComponents()
        return koreanLayout
    }


    fun modeChange(){
        if(isCaps){
            isCaps = false
            capsView?.setImageResource(R.drawable.ic_caps_unlock)
            for(button in buttons){
                when(button.text.toString()){
                    "ㅃ" -> {
                        button.text = "ㅂ"
                    }
                    "ㅉ" -> {
                        button.text = "ㅈ"
                    }
                    "ㄸ" -> {
                        button.text = "ㄷ"
                    }
                    "ㄲ" -> {
                        button.text = "ㄱ"
                    }
                    "ㅆ" -> {
                        button.text = "ㅅ"
                    }
                    "ㅒ" -> {
                        button.text = "ㅐ"
                    }
                    "ㅖ" -> {
                        button.text = "ㅔ"
                    }
                }
            }
        }
        else{
            isCaps = true
            capsView?.setImageResource(R.drawable.ic_caps_lock)
            for(button in buttons){
                when(button.text.toString()){
                    "ㅂ" -> {
                        button.text = "ㅃ"
                    }
                    "ㅈ" -> {
                        button.text = "ㅉ"
                    }
                    "ㄷ" -> {
                        button.text = "ㄸ"
                    }
                    "ㄱ" -> {
                        button.text = "ㄲ"
                    }
                    "ㅅ" -> {
                        button.text = "ㅆ"
                    }
                    "ㅐ" -> {
                        button.text = "ㅒ"
                    }
                    "ㅔ" -> {
                        button.text = "ㅖ"
                    }
                }
            }
        }
    }


    private fun playClick(i: Int) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        when (i) {
            32 -> am!!.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR)
            Keyboard.KEYCODE_DONE, 10 -> am!!.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN)
            Keyboard.KEYCODE_DELETE -> am!!.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE)
            else -> am!!.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, -1.toFloat())
        }
    }

    private fun playVibrate(){
        if(vibrate > 0){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(70, vibrate))
            }
            else{
                vibrator.vibrate(70)
            }
        }
    }

    private fun getMyClickListener(actionButton:Button):View.OnClickListener{

        val clickListener = (View.OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                inputConnection?.requestCursorUpdates(InputConnection.CURSOR_UPDATE_IMMEDIATE)
            }
            playVibrate()
            val cursorcs:CharSequence? =  inputConnection?.getSelectedText(InputConnection.GET_TEXT_WITH_STYLES)
            if(cursorcs != null && cursorcs.length >= 2){

                val eventTime = SystemClock.uptimeMillis()
                inputConnection?.finishComposingText()
                inputConnection?.sendKeyEvent(KeyEvent(eventTime, eventTime,
                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD))
                inputConnection?.sendKeyEvent(KeyEvent(SystemClock.uptimeMillis(), eventTime,
                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD))
                hangulMaker.clear()
            }
            when (actionButton.text.toString()) {

                else -> {
                    playClick(actionButton.text.toString().toCharArray().get(0).toInt())
                    try{
                        val myText = Integer.parseInt(actionButton.text.toString())
                        hangulMaker.directlyCommit()
                        inputConnection?.commitText(actionButton.text.toString(), 1)
                    }catch (e:NumberFormatException){
                        hangulMaker.commit(actionButton.text.toString().toCharArray().get(0))
                    }
                    if(isCaps){
                        modeChange()
                    }

                    sendText()

                }
            }
        })
        actionButton.setOnClickListener(clickListener)
        return clickListener
    }

    fun getOnTouchListener(clickListener: View.OnClickListener):View.OnTouchListener{
        val handler = Handler()
        val initailInterval = 500
        val normalInterval = 100
        val handlerRunnable = object: Runnable{
            override fun run() {
                handler.postDelayed(this, normalInterval.toLong())
                clickListener.onClick(downView)
            }
        }
        val onTouchListener = object:View.OnTouchListener {
            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                when (motionEvent?.getAction()) {
                    MotionEvent.ACTION_DOWN -> {
                        handler.removeCallbacks(handlerRunnable)
                        handler.postDelayed(handlerRunnable, initailInterval.toLong())
                        downView = view!!
                        clickListener.onClick(view)
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        handler.removeCallbacks(handlerRunnable)
                        downView = null
                        return true
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        handler.removeCallbacks(handlerRunnable)
                        downView = null
                        return true
                    }
                }
                return false
            }
        }

        return onTouchListener
    }

    private fun setLayoutComponents(){
        for(line in layoutLines.indices){
            val children = layoutLines[line].children.toList()
            val myText = myKeysText[line]
            val tempList = mutableListOf<Int>()
            var longClickIndex = 0
            for(item in children.indices){
                val actionButton = children[item].findViewById<Button>(R.id.key_button)
                val specialKey = children[item].findViewById<ImageView>(R.id.special_key)
                var myOnClickListener:View.OnClickListener? = null
                when(myText[item]){
                    "space" -> {
                        specialKey.setImageResource(R.drawable.ic_space_bar)
                        specialKey.visibility = View.VISIBLE
                        actionButton.visibility = View.GONE
                        myOnClickListener = getSpaceAction()
                        specialKey.setOnClickListener(myOnClickListener)
//                        specialKey.setOnTouchListener(getOnTouchListener(myOnClickListener))
                        specialKey.setBackgroundResource(R.drawable.key_background)
                    }
                    "DEL" -> {
                        specialKey.setImageResource(R.drawable.del)
                        specialKey.visibility = View.VISIBLE
                        actionButton.visibility = View.GONE
                        myOnClickListener = getDeleteAction()
                        specialKey.setOnClickListener(myOnClickListener)
                        specialKey.setOnTouchListener(getOnTouchListener(myOnClickListener))
                    }
                    "CAPS" -> {
                        specialKey.setImageResource(R.drawable.ic_caps_unlock)
                        specialKey.visibility = View.VISIBLE
                        actionButton.visibility = View.GONE
                        capsView = specialKey
                        myOnClickListener = getCapsAction()
                        specialKey.setOnClickListener(myOnClickListener)
                        specialKey.setBackgroundResource(R.drawable.key_background)
                    }
                    "Enter" -> {
                        specialKey.setImageResource(R.drawable.ic_enter)
                        specialKey.visibility = View.VISIBLE
                        actionButton.visibility = View.GONE
                        if (isFirst){
                            myOnClickListener = getEnterAction()
                            specialKey.setOnClickListener(myOnClickListener)
                            specialKey.setOnTouchListener(getOnTouchListener(myOnClickListener))
                            specialKey.setBackgroundResource(R.drawable.key_background)
                        }
                    }
                    "한/영" -> {
                        actionButton.text = myText[item]
                        buttons.add(actionButton)
                        myOnClickListener = object :View.OnClickListener{
                            override fun onClick(p0: View?) {
                                keyboardInterationListener.modechange(0)
                            }
                        }
                        actionButton.setOnClickListener(myOnClickListener)
                    }
                    "!#1" -> {
                        actionButton.text = myText[item]
                        buttons.add(actionButton)
                        myOnClickListener = object :View.OnClickListener{
                            override fun onClick(p0: View?) {
                                keyboardInterationListener.modechange(2)
                            }
                        }
                        actionButton.setOnClickListener(myOnClickListener)
                    }
                    else -> {
                        tempList.add(item)
                        actionButton.text = myText[item]
                        buttons.add(actionButton)
                        myOnClickListener = getMyClickListener(actionButton)
                        actionButton.setOnTouchListener(getOnTouchListener(myOnClickListener))
                    }
                }
                children[item].setOnClickListener(myOnClickListener)
            }
        }
    }
    fun getSpaceAction():View.OnClickListener{
        return View.OnClickListener{
            playClick('ㅂ'.toInt())
            playVibrate()
            hangulMaker.commitSpace()

            sendText()
        }
    }

    fun getDeleteAction():View.OnClickListener{
        return View.OnClickListener{
            playVibrate()
            val cursorcs:CharSequence? =  inputConnection?.getSelectedText(InputConnection.GET_TEXT_WITH_STYLES)
            if(cursorcs != null && cursorcs.length >= 2){

                val eventTime = SystemClock.uptimeMillis()
                inputConnection?.finishComposingText()
                inputConnection?.sendKeyEvent(KeyEvent(eventTime, eventTime,
                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD))
                inputConnection?.sendKeyEvent(KeyEvent(SystemClock.uptimeMillis(), eventTime,
                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD))
                hangulMaker.clear()
            }
            else{
                hangulMaker.delete()
            }
            sendText()
        }
    }

    fun getCapsAction():View.OnClickListener{
        return View.OnClickListener{
            playVibrate()
            modeChange()
        }
    }

    //엔터 키 눌렸을 때
    fun getEnterAction():View.OnClickListener{
        return View.OnClickListener{
            playVibrate()
            hangulMaker.directlyCommit()
            val eventTime = SystemClock.uptimeMillis()

            enterText()

            GlobalScope.launch(Dispatchers.Main){
                delay(1000)
                //key ActionDown --> 키 눌렸을 때
                inputConnection?.sendKeyEvent(KeyEvent(eventTime, eventTime,
                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD))

                //key ActionUp --> 눌린 키 떼지도록
                inputConnection?.sendKeyEvent(KeyEvent(SystemClock.uptimeMillis(), eventTime,
                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD))

                if (mode == 2){
                    //키보드 무작위 배치
                    keyboardInterationListener.modechange(1)
                }
            }

        }
    }

    fun sendText(){
        val text = inputConnection?.getExtractedText(ExtractedTextRequest(), InputConnection.GET_TEXT_WITH_STYLES)
        keyboardInterationListener.sendText(text?.text.toString())
    }

    fun enterText() {
        val text = inputConnection?.getExtractedText(ExtractedTextRequest(), InputConnection.GET_TEXT_WITH_STYLES)
        keyboardInterationListener.checkText(text?.text.toString())
    }
}
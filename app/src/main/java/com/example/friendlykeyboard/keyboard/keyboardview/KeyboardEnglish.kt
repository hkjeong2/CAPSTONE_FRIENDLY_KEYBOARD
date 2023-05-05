package com.example.friendlykeyboard.keyboard.keyboardview

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.os.*
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.example.friendlykeyboard.R
import com.example.friendlykeyboard.keyboard.KeyboardInteractionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class KeyboardEnglish constructor(var context: Context, var layoutInflater: LayoutInflater, var keyboardInterationListener: KeyboardInteractionListener) {
    lateinit var englishLayout: LinearLayout
    var inputConnection: InputConnection? = null
        set(inputConnection){
            field = inputConnection
        }
    lateinit var vibrator: Vibrator
    lateinit var sharedPreferences: SharedPreferences
    var isCaps:Boolean = false
    var buttons:MutableList<Button> = mutableListOf<Button>()
    var modeButtons:MutableList<Button> = mutableListOf<Button>()
    var blockMode:Boolean = false

    //keyboard layout의 네 줄의 각 문자 item을 바인딩 시키기 위한 item 준비
    val numpadText = listOf<String>("1","2","3","4","5","6","7","8","9","0")
    val firstLineText = listOf<String>("q","w","e","r","t","y","u","i","o","p")
    val secondLineText = listOf<String>("a","s","d","f","g","h","j","k","l")
    val thirdLineText = listOf<String>("CAPS","z","x","c","v","b","n","m","DEL")
    val fourthLineText = listOf<String>("!#1","한/영",",","space",".","Enter")
    val firstLongClickText = listOf("!","@","#","$","%","^","&","*","(",")")
    val secondLongClickText = listOf<String>("~","+","-","×","♥",":",";","'","\"")
    val thirdLongClickText = listOf("∞","_","<",">","/",",","?")
    val myKeysText = ArrayList<List<String>>()
    val myLongClickKeysText = ArrayList<List<String>>()
    val layoutLines = ArrayList<LinearLayout>()

    var downView: View? = null
    var sound = 0
    var vibrate = 0
    var capsView: ImageView? = null

    lateinit var numpadLine : LinearLayout
    lateinit var firstLine: LinearLayout
    lateinit var secondLine: LinearLayout
    lateinit var thirdLine: LinearLayout
    lateinit var fourthLine: LinearLayout

    // 제재 기능 : 영어 입력만 가능
    fun setChangingModeAvailability(possible : Boolean){
        if (!possible){
            Toast.makeText(context, "제재 : 강제 영문 키보드 활성화", Toast.LENGTH_SHORT).show()
            blockMode = true
            for (i in modeButtons.indices){
                modeButtons[i].text = ""
            }
        }
        else{
            blockMode = false
            val list : List<String> = listOf("!#1", "한/영")
            for (i in modeButtons.indices){
                modeButtons[i].text = list[i]
            }
        }
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
        englishLayout.setPadding(paddingLeft, 0, paddingRight, paddingBottom)

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
        englishLayout.setBackgroundColor(keyboardBackgroundColor)
    }

    //키보드 (view) 및 (클릭 시 기능) 초기화
    fun init() {
        englishLayout = layoutInflater.inflate(R.layout.keyboard_action, null) as LinearLayout
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE)
        sound = sharedPreferences.getInt("keyboardSound", -1)
        vibrate = sharedPreferences.getInt("keyboardVibrate", -1)

        numpadLine = englishLayout.findViewById<LinearLayout>(
            R.id.numpad_line
        )
        firstLine = englishLayout.findViewById<LinearLayout>(
            R.id.first_line
        )
        secondLine = englishLayout.findViewById<LinearLayout>(
            R.id.second_line
        )
        thirdLine = englishLayout.findViewById<LinearLayout>(
            R.id.third_line
        )
        fourthLine = englishLayout.findViewById<LinearLayout>(
            R.id.fourth_line
        )

        updateKeyboard()

        //keyboard 위치별 각 문자
        myKeysText.clear()
        myKeysText.add(numpadText)
        myKeysText.add(firstLineText)
        myKeysText.add(secondLineText)
        myKeysText.add(thirdLineText)
        myKeysText.add(fourthLineText)

        //특정 위치의 keyboard 길게 눌렀을 때의 문자
        myLongClickKeysText.clear()
        myLongClickKeysText.add(firstLongClickText)
        myLongClickKeysText.add(secondLongClickText)
        myLongClickKeysText.add(thirdLongClickText)

        //keyboard의 각 줄 layout
        layoutLines.clear()
        layoutLines.add(numpadLine)
        layoutLines.add(firstLine)
        layoutLines.add(secondLine)
        layoutLines.add(thirdLine)
        layoutLines.add(fourthLine)

        setLayoutComponents()
    }

    fun getLayout(): LinearLayout {
        return englishLayout
    }

    //Caps 클릭 시 변경
    fun modeChange(){
        if(isCaps){
            isCaps = false
            capsView?.setImageResource(R.drawable.ic_caps_unlock)
            for(button in buttons){
                button.setText(button.text.toString().toLowerCase())
            }
        }
        else{
            isCaps = true
            capsView?.setImageResource(R.drawable.ic_caps_lock)
            for(button in buttons){
                button.setText(button.text.toString().toUpperCase())
            }
        }
    }

    // 타이핑 소리 효과
    private fun playClick(i: Int) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        when (i) {
            32 -> am!!.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR)
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

    private fun getMyLongClickListener(textView: TextView): View.OnLongClickListener{
        //아래 getMyClickListener와 동일한 방식
        //다만 길게 눌렸을 때 케이스
        val longClickListener = object: View.OnLongClickListener{
            override fun onLongClick(p0: View?): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    inputConnection?.requestCursorUpdates(InputConnection.CURSOR_UPDATE_IMMEDIATE)
                }
                playVibrate()
                val cursorcs:CharSequence? =  inputConnection?.getSelectedText(InputConnection.GET_TEXT_WITH_STYLES)
                if(cursorcs != null && cursorcs.length >= 2){

                    val eventTime = SystemClock.uptimeMillis()
                    inputConnection?.finishComposingText()
                    inputConnection?.sendKeyEvent(
                        KeyEvent(eventTime, eventTime,
                        KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
                        KeyEvent.FLAG_SOFT_KEYBOARD)
                    )
                    inputConnection?.sendKeyEvent(
                        KeyEvent(
                            SystemClock.uptimeMillis(), eventTime,
                        KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
                        KeyEvent.FLAG_SOFT_KEYBOARD)
                    )
                }
                when (textView.text.toString()) {
                    "한/영" -> {
                        keyboardInterationListener.modechange(1)
                    }
                    "!#1" -> {
                        keyboardInterationListener.modechange(2)
                    }
                    else -> {
                        playClick(textView.text.toString().toCharArray().get(0).toInt())
                        inputConnection?.commitText(textView.text.toString(), 1)

                        sendText()
                    }
                }
                return true
            }
        }
        return longClickListener
    }

    private fun getMyClickListener(actionButton: Button): View.OnClickListener{
        // 아래의 두 방법을 통해 write text
        // 1) 각각의 키 이벤트를 전송하여 어플리케이션으로 텍스트 전송 (두 문자 이상의 문장을 꾹 눌러서 텍스트 블락 생성 시)
        // 2) inputConnection을 통해 직접 textfield 수정
        val clickListener = (View.OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                inputConnection?.requestCursorUpdates(InputConnection.CURSOR_UPDATE_IMMEDIATE)
            }
            playVibrate()
            val cursorcs:CharSequence? =  inputConnection?.getSelectedText(InputConnection.GET_TEXT_WITH_STYLES)
            if(cursorcs != null && cursorcs.length >= 2){

                // 1)선택중인 블럭이 존재한다면 해당 블럭을 삭제하고 텍스트를 입력할 수 있도록
                val eventTime = SystemClock.uptimeMillis()
                inputConnection?.finishComposingText()
                inputConnection?.sendKeyEvent(
                    KeyEvent(eventTime, eventTime,
                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD)
                )
                inputConnection?.sendKeyEvent(
                    KeyEvent(
                        SystemClock.uptimeMillis(), eventTime,
                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD)
                )
                inputConnection?.sendKeyEvent(
                    KeyEvent(eventTime, eventTime,
                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD)
                )
                inputConnection?.sendKeyEvent(
                    KeyEvent(
                        SystemClock.uptimeMillis(), eventTime,
                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD)
                )

            }
            when (actionButton.text.toString()) {
                "한/영" -> {
                    keyboardInterationListener.modechange(1)
                }
                "!#1" -> {
                    keyboardInterationListener.modechange(2)
                }
                else -> {
                    if (actionButton.text.toString().length >= 1){
                        playClick(
                            actionButton.text.toString().toCharArray().get(
                                0
                            ).toInt()
                        )
                        inputConnection?.commitText(actionButton.text,1)

                        sendText()
                    }
                }
            }

        })
        actionButton.setOnClickListener(clickListener)
        return clickListener
    }

    fun getOnTouchListener(clickListener: View.OnClickListener): View.OnTouchListener{
        val handler = Handler()
        val initailInterval = 500
        val normalInterval = 100
        val handlerRunnable = object: Runnable{
            override fun run() {
                handler.postDelayed(this, normalInterval.toLong())
                clickListener.onClick(downView)
            }
        }
        val onTouchListener = object: View.OnTouchListener {
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

    //준비된 개별 문자(item)들의 클릭 리스너 및 view를 keyboard에 적용
    private fun setLayoutComponents(){
        //keyboard의 각 줄, layout들을 iterate
        for(line in layoutLines.indices){
            //각 줄의 layout이 include 했던 view들의 집합을 children으로 정의
            val children = layoutLines[line].children.toList()
            val myText = myKeysText[line]
            var longClickIndex = 0

            //각 줄 즉 layout의 각각의 item들 iterate
            for(item in children.indices){
                // keyboard의 개별 item 버튼
                val actionButton = children[item].findViewById<Button>(R.id.key_button)
                // 문자 대신 image가 필요한 자판 (space, DEL, CAPS)
                val specialKey = children[item].findViewById<ImageView>(R.id.special_key)
                var myOnClickListener: View.OnClickListener? = null
                when(myText[item]){
                    "space" -> {
                        specialKey.setImageResource(R.drawable.ic_space_bar)
                        specialKey.visibility = View.VISIBLE
                        actionButton.visibility = View.GONE
                        myOnClickListener = getSpaceAction()
                        specialKey.setOnClickListener(myOnClickListener)
                        specialKey.setOnTouchListener(getOnTouchListener(myOnClickListener))
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
                        specialKey.setOnTouchListener(getOnTouchListener(myOnClickListener))
                        specialKey.setBackgroundResource(R.drawable.key_background)
                    }
                    "Enter" -> {
                        specialKey.setImageResource(R.drawable.ic_enter)
                        specialKey.visibility = View.VISIBLE
                        actionButton.visibility = View.GONE
                        myOnClickListener = getEnterAction()
                        specialKey.setOnClickListener(myOnClickListener)
                        specialKey.setOnTouchListener(getOnTouchListener(myOnClickListener))
                        specialKey.setBackgroundResource(R.drawable.key_background)
                    }
                    else -> {
                        //특수 key들 제외한 일반 문자들 대상
                        val longClickTextView = children[item].findViewById<TextView>(R.id.text_long_click)
                        actionButton.text = myText[item]
                        buttons.add(actionButton)
                        myOnClickListener = getMyClickListener(actionButton)

                        // mode 잠금 할 수 있도록 따로 저장
                        if (actionButton.text == "한/영" || actionButton.text == "!#1")
                            modeButtons.add(actionButton)

                        if(line > 0 && line < 4){//특수기호가 삽입될 수 있는 layout의 라인 (1~3번째 키보드 줄)
                            //길게 눌렸을 때
                            longClickTextView.setText(myLongClickKeysText[line - 1].get(longClickIndex++))
                            longClickTextView.bringToFront()
                            longClickTextView.setOnClickListener(myOnClickListener)
                            actionButton.setOnLongClickListener(getMyLongClickListener(longClickTextView))
                            longClickTextView.setOnLongClickListener(getMyLongClickListener(longClickTextView))
                        }
                    }
                }
                children[item].setOnClickListener(myOnClickListener)
            }
        }
    }
    fun getSpaceAction(): View.OnClickListener{
        return View.OnClickListener{
            playClick('ㅂ'.toInt())
            playVibrate()
            inputConnection?.commitText(" ",1)

            sendText()
        }
    }

    fun getDeleteAction(): View.OnClickListener{
        return View.OnClickListener{
            playVibrate()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                inputConnection?.deleteSurroundingTextInCodePoints(1, 0)
            }else{
                inputConnection?.deleteSurroundingText(1,0)
            }
            sendText()
        }
    }

    fun getCapsAction(): View.OnClickListener{
        return View.OnClickListener{
            playVibrate()
            modeChange()
        }
    }

    fun getEnterAction(): View.OnClickListener{
        return View.OnClickListener{
            if (inputConnection?.getExtractedText(ExtractedTextRequest(), InputConnection.GET_TEXT_WITH_STYLES)?.text.toString().length >= 1){
                playVibrate()
                val eventTime = SystemClock.uptimeMillis()
                enterText()

                //key ActionDown --> 키 눌렸을 때
                inputConnection?.sendKeyEvent(KeyEvent(eventTime, eventTime,
                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD))

                //key ActionUp --> 눌린 키 떼지도록
                inputConnection?.sendKeyEvent(KeyEvent(SystemClock.uptimeMillis(), eventTime,
                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0, 0, 0, 0,
                    KeyEvent.FLAG_SOFT_KEYBOARD))
            }
        }
    }

    fun sendText(){
        val text = inputConnection?.getExtractedText(ExtractedTextRequest(), InputConnection.GET_TEXT_WITH_STYLES)
        keyboardInterationListener.sendText(text?.text.toString())
    }

    fun enterText(){
        val text = inputConnection?.getExtractedText(ExtractedTextRequest(), InputConnection.GET_TEXT_WITH_STYLES)
        keyboardInterationListener.checkText(text?.text.toString())
    }

}
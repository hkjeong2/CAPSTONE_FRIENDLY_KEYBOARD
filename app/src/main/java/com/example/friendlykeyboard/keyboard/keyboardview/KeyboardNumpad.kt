package com.example.friendlykeyboard.keyboard.keyboardview

import android.content.Context
import android.content.res.Configuration
import android.inputmethodservice.Keyboard
import android.media.AudioManager
import android.os.Build
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.children
import com.example.friendlykeyboard.*
import com.example.friendlykeyboard.keyboard.KeyboardInteractionListener

class KeyboardNumpad constructor(var context: Context, var layoutInflater: LayoutInflater, var keyboardInterationListener: KeyboardInteractionListener){

    lateinit var numpadLayout:LinearLayout
    lateinit var vibrator: Vibrator
    var inputConnection:InputConnection? = null
        set(inputConnection){
            field = inputConnection
        }
    var buttons:MutableList<Button> = mutableListOf<Button>()

    val firstLineText = listOf<String>("1", "2", "3","DEL")
    val secondLineText = listOf<String>("4", "5", "6", "Enter")
    val thirdLineText = listOf<String>("7","8","9",".")
    val fourthLineText = listOf<String>("-", "0", ",", "")
    val myKeysText = ArrayList<List<String>>()
    val layoutLines = ArrayList<LinearLayout>()

    lateinit var firstLine: LinearLayout
    lateinit var secondLine: LinearLayout
    lateinit var thirdLine: LinearLayout
    lateinit var fourthLine: LinearLayout

    fun updateKeyboard(){
        val sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE)
        val height = sharedPreferences.getInt("keyboardHeight", 150)
        val paddingLeft = sharedPreferences.getInt("keyboardPaddingLeft", 0)
        val paddingRight = sharedPreferences.getInt("keyboardPaddingRight", 0)
        val paddingBottom = sharedPreferences.getInt("keyboardPaddingBottom", 0)
        val fontColor = sharedPreferences.getInt("keyboardFontColor", 0)
        val keyboardColor = sharedPreferences.getInt("keyboardColor", 0)
        val keyboardBackgroundColor = sharedPreferences.getInt("keyboardBackground", 0)
        val config = context.getResources().configuration

        numpadLayout.setPadding(paddingLeft, 0, paddingRight, paddingBottom)

        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){
            firstLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height*0.7).toInt())
            secondLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height*0.7).toInt())
            thirdLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height*0.7).toInt())
            fourthLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height*0.7).toInt())
        }else{
            firstLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
            secondLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
            thirdLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
            fourthLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
        }
        // 키보드 폰트, 자판 색, 폰트 색 업데이트
        for (button in buttons) {
            button.setTextColor(fontColor)
            button.background.setTint(keyboardColor)
            // 키보드 배경색 업데이트
            numpadLayout.setBackgroundColor(keyboardBackgroundColor)
        }
    }

    fun init() {
        numpadLayout = layoutInflater.inflate(R.layout.keyboard_numpad, null) as LinearLayout
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        firstLine = numpadLayout.findViewById<LinearLayout>(
            R.id.first_line
        )
        secondLine = numpadLayout.findViewById<LinearLayout>(
            R.id.second_line
        )
        thirdLine = numpadLayout.findViewById<LinearLayout>(
            R.id.third_line
        )
        fourthLine = numpadLayout.findViewById<LinearLayout>(
            R.id.fourth_line
        )

        updateKeyboard()

        myKeysText.clear()
        myKeysText.add(firstLineText)
        myKeysText.add(secondLineText)
        myKeysText.add(thirdLineText)
        myKeysText.add(fourthLineText)

        layoutLines.clear()
        layoutLines.add(firstLine)
        layoutLines.add(secondLine)
        layoutLines.add(thirdLine)
        layoutLines.add(fourthLine)

        setLayoutComponents()
    }

    fun getLayout(): LinearLayout {
        return numpadLayout
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


    private fun setLayoutComponents(){
        val sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE)
        val sound = sharedPreferences.getInt("keyboardSound", -1)
        val vibrate = sharedPreferences.getInt("keyboardVibrate", -1)
        for(line in layoutLines.indices){
            val children = layoutLines[line].children.toList()
            val myText = myKeysText[line]
            for(item in children.indices){
                val actionButton = children[item].findViewById<Button>(R.id.key_button)
                actionButton.text = myText[item]

                buttons.add(actionButton)

                val clickListener = (View.OnClickListener {
                    if(vibrate > 0){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(70, vibrate))
                        }
                        else{
                            vibrator.vibrate(70)
                        }
                    }

                    when (actionButton.text.toString()) {

                        "DEL" -> {
                            inputConnection?.deleteSurroundingText(1,0)
                        }
                        "Enter" -> {
                            val eventTime = SystemClock.uptimeMillis()
                            inputConnection?.sendKeyEvent(KeyEvent(eventTime, eventTime,
                                KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0, 0, 0, 0,
                                KeyEvent.FLAG_SOFT_KEYBOARD))
                            inputConnection?.sendKeyEvent(KeyEvent(
                                SystemClock.uptimeMillis(), eventTime,
                                KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0, 0, 0, 0,
                                KeyEvent.FLAG_SOFT_KEYBOARD))
                        }
                        else -> {
                            if (actionButton.text.toString().length >= 1){
                                playClick(
                                    actionButton.text.toString().toCharArray().get(0).toInt()
                                )
                                inputConnection?.commitText(actionButton.text,1)
                            }
                        }
                    }
                })

                actionButton.setOnClickListener(clickListener)
                children[item].setOnClickListener(clickListener)

            }
        }
    }

}
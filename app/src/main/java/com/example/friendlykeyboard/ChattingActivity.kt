package com.example.friendlykeyboard

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.friendlykeyboard.databinding.ActivityChattingBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChattingActivity : AppCompatActivity() {
    lateinit var binding : ActivityChattingBinding
    lateinit var spf : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    private fun initStage(){
        val stage = spf.getInt("stageNumber", 0)
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
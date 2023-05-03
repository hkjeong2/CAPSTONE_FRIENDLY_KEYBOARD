package com.example.friendlykeyboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.friendlykeyboard.databinding.ActivityChattingBinding

class ChattingActivity : AppCompatActivity() {
    lateinit var binding : ActivityChattingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }


}
package com.example.friendlykeyboard.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.friendlykeyboard.SettingsKeyboardBackgroundActivity
import com.example.friendlykeyboard.SettingsKeyboardColorActivity
import com.example.friendlykeyboard.SettingsKeyboardFontActivity
import com.example.friendlykeyboard.SettingsKeyboardSizeActivity
import com.example.friendlykeyboard.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            when (it.resultCode) {
                100 -> {
                    // 키보드 크기 설정
                    val intent = it.data!!
                    val height = intent.getStringExtra("height")
                    val paddingWidth = intent.getStringExtra("paddingWidth")
                    val paddingBottom = intent.getStringExtra("paddingBottom")

                    binding.settingsItemKeyboardSize.attribute.text = buildString  {
                        append(height)
                        append(", ")
                        append(paddingWidth)
                        append(", ")
                        append(paddingBottom)
                    }
                }
                200 -> {
                    // 키보드 글자 설정
                    val intent = it.data!!
                    val size = intent.getStringExtra("size")
                    val font = intent.getStringExtra("font")

                    binding.settingsItemKeyboardFont.attribute.text = buildString {
                        append(size)
                        append(", ")
                        append(font)
                    }
                }
                300 -> {
                    // 키보드 색상 설정
                    val intent = it.data!!
                    binding.settingsItemKeyboardColor.attribute.text = intent.getStringExtra("color")
                }
                400 -> {
                    // 키보드 배경색 설정
                    val intent = it.data!!
                    binding.settingsItemKeyboardBackground.attribute.text = intent.getStringExtra("background")
                }
                else -> {}
            }
        }

        initClickListener()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initClickListener() {
        binding.settingsItemKeyboardSize.item.setOnClickListener {
            resultLauncher.launch(Intent(activity, SettingsKeyboardSizeActivity::class.java))
        }

        binding.settingsItemKeyboardFont.item.setOnClickListener {
            resultLauncher.launch(Intent(activity, SettingsKeyboardFontActivity::class.java))
        }

        binding.settingsItemKeyboardColor.item.setOnClickListener {
            resultLauncher.launch(Intent(activity, SettingsKeyboardColorActivity::class.java))
        }

        binding.settingsItemKeyboardBackground.item.setOnClickListener {
            resultLauncher.launch(Intent(activity, SettingsKeyboardBackgroundActivity::class.java))
        }
    }
}
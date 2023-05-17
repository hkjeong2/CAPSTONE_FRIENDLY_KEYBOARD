package com.example.friendlykeyboard.fragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.friendlykeyboard.*
import com.example.friendlykeyboard.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        pref = requireActivity().getSharedPreferences("setting", Activity.MODE_PRIVATE)
        editor = pref.edit()

        initClickListener()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        //키보드 설정
        val height = pref.getInt("keyboardHeight", 150) - 50
        val paddingLeft = pref.getInt("keyboardPaddingLeft", 0)
        val paddingRight = pref.getInt("keyboardPaddingRight", 0)
        val paddingBottom = pref.getInt("keyboardPaddingBottom", 0)
        binding.settingsItemKeyboardSize.attribute.text =
            "높이: ${height}%, 좌측: ${paddingLeft}dp, 우측: ${paddingRight}dp, 하단: ${paddingBottom}dp"

        val fontColor = pref.getInt("keyboardFontColor", Color.parseColor("#FF018786"))
        with (binding.settingsItemKeyboardFont) {
            imageView.drawable.setTint(fontColor)
            attribute.setTextColor(fontColor)
        }

        val color = pref.getInt("keyboardColor", Color.parseColor("#FFBB86FC"))
        with (binding.settingsItemKeyboardColor) {
            imageView.drawable.setTint(color)
            attribute.setTextColor(color)
        }

        val background_color = pref.getInt("keyboardBackground", Color.parseColor("#86BBFC"))
        with (binding.settingsItemKeyboardBackground) {
            imageView.drawable.setTint(background_color)
            attribute.setTextColor(background_color)
        }

        //기능 설정
        val settingAlarmColor = pref.getInt("settingAlarmColor", Color.parseColor("#000000"))
        binding.stage1Text.setTextColor(settingAlarmColor)
        binding.stage1.setTextColor(settingAlarmColor)
        //binding.stage1Img.drawable.setTint(settingAlarmColor)

        val settingCorrectColor = pref.getInt("settingCorrectColor", Color.parseColor("#000000"))
        binding.stage2Text.setTextColor(settingCorrectColor)
        binding.stage2.setTextColor(settingCorrectColor)
        //binding.stage2Img.drawable.setTint(settingCorrectColor)

        val settingInvisibleColor = pref.getInt("settingInvisibleColor", Color.parseColor("#000000"))
        binding.stage21Text.setTextColor(settingInvisibleColor)
        binding.stage21.setTextColor(settingInvisibleColor)

        val settingEnglishColor = pref.getInt("settingEnglishColor", Color.parseColor("#000000"))
        binding.stage22Text.setTextColor(settingEnglishColor)
        binding.stage22.setTextColor(settingEnglishColor)

        val settingRandomColor = pref.getInt("settingRandomColor", Color.parseColor("#000000"))
        binding.stage23Text.setTextColor(settingRandomColor)
        binding.stage23.setTextColor(settingRandomColor)

        //대체어 설정
        val candidateFontColor = pref.getInt("candidateFontColor", Color.parseColor("#000000"))
        with (binding.settingsCandidateFont) {
            imageView.drawable.setTint(candidateFontColor)
            attribute.setTextColor(candidateFontColor)
        }

        val candidateButtonColor = pref.getInt("candidateButtonColor", Color.parseColor("#d3d3d3"))
        with (binding.settingsCandidateButtonColor) {
            imageView.drawable.setTint(candidateButtonColor)
            attribute.setTextColor(candidateButtonColor)
        }

        val candidateLayoutColor = pref.getInt("candidateLayoutColor", Color.parseColor("#dddddd"))
        with (binding.settingsCandidateLayoutColor) {
            imageView.drawable.setTint(candidateLayoutColor)
            attribute.setTextColor(candidateLayoutColor)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initClickListener() {
        binding.settingsItemKeyboardSize.item.setOnClickListener {
            startActivity(Intent(activity, SettingsKeyboardSizeActivity::class.java))
        }

        binding.settingsItemKeyboardFont.item.setOnClickListener {
            if (pref.getInt("stageNumber", 0) != 2){
                startActivity(Intent(activity, SettingsKeyboardFontActivity::class.java))
            }
            else{
                Toast.makeText(requireContext(), "투명 모드 입니다!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.settingsItemKeyboardColor.item.setOnClickListener {
            if (pref.getInt("stageNumber", 0) != 2){
                startActivity(Intent(activity, SettingsKeyboardColorActivity::class.java))
            }else{
                Toast.makeText(requireContext(), "투명 모드 입니다!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.settingsItemKeyboardBackground.item.setOnClickListener {
            startActivity(Intent(activity, SettingsKeyboardBackgroundActivity::class.java))
        }

        binding.settingsCandidateFont.item.setOnClickListener{
            startActivity(Intent(activity, SettingsCandidateFontActivity::class.java))
        }

        binding.settingsCandidateButtonColor.item.setOnClickListener{
            startActivity(Intent(activity, SettingsCandidateButtonColorActivity::class.java))
        }

        binding.settingsCandidateLayoutColor.item.setOnClickListener{
            startActivity(Intent(activity, SettingsCandidateLayoutColorActivity::class.java))
        }
    }

}
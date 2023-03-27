package com.example.friendlykeyboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.friendlykeyboard.databinding.ActivitySettingsKeyboardColorBinding
import com.flask.colorpicker.builder.ColorPickerClickListener

class SettingsKeyboardColorActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsKeyboardColorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsKeyboardColorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        with (supportActionBar!!) {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_action_arrow_back)
            title = "키보드 색상"
        }

        binding.colorPickerView.addOnColorChangedListener {
            // TODO: 키보드 색상 수정
            //binding.tempTextView.text = binding.colorPickerView.selectedColor.toString(16)
            //binding.tempTextView.setBackgroundColor(binding.colorPickerView.selectedColor)
        }


        /*
        binding.button.setOnClickListener {
            val intent = Intent().apply {
                putExtra("color", "색상 #FFFFFF")
            }
            setResult(300, intent)
            finish()
        }
        */
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            setResult(RESULT_CANCELED)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
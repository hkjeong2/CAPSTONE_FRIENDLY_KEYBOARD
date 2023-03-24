package com.example.friendlykeyboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.friendlykeyboard.databinding.ActivitySettingsKeyboardColorBinding

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


        val colorPicker = binding.colorPicker
        val saturationBar = binding.saturationBar
        val opacityBar = binding.opacityBar

        with(colorPicker) {
            addSaturationBar(saturationBar)
            addOpacityBar(opacityBar)
            showOldCenterColor = false
        }

        // To set the old selected color you can do it like this
        // colorPicker.oldCenterColor = colorPicker.color

        // adds listener to the colorpicker which is implemented in the activity
        colorPicker.setOnColorChangedListener {
            // TODO
        }

        // adding onChangeListeners to bars
        saturationBar.setOnSaturationChangedListener {
            // TODO
        }
        opacityBar.setOnOpacityChangedListener {
            // TODO
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
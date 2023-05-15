package com.example.friendlykeyboard

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class MyMarkerView(context: Context?, layoutResource: Int) : MarkerView(context, layoutResource) {
    lateinit var dateData: Array<String>
    lateinit var markerData: Array<IntArray>

    override fun refreshContent(e: Entry, highlight: Highlight?) {
        val dateTextView = findViewById<TextView>(R.id.dateTextView)
        val countTextView0 = findViewById<TextView>(R.id.countTextView0)
        val countTextView1 = findViewById<TextView>(R.id.countTextView1)
        val countTextView2 = findViewById<TextView>(R.id.countTextView2)
        val countTextView3 = findViewById<TextView>(R.id.countTextView3)
        val countTextView4 = findViewById<TextView>(R.id.countTextView4)
        val countTextView5 = findViewById<TextView>(R.id.countTextView5)
        val countTextView6 = findViewById<TextView>(R.id.countTextView6)
        val countTextView7 = findViewById<TextView>(R.id.countTextView7)
        val countTextView8 = findViewById<TextView>(R.id.countTextView8)

        val index = e.x.toInt()
        dateTextView.text = dateData[index]
        countTextView0.text = "여성/가족: ${markerData[index][0]}"
        countTextView1.text = "남성: ${markerData[index][1]}"
        countTextView2.text = "성소수자: ${markerData[index][2]}"
        countTextView3.text = "인종/국적: ${markerData[index][3]}"
        countTextView4.text = "연령: ${markerData[index][4]}"
        countTextView5.text = "지역: ${markerData[index][5]}"
        countTextView6.text = "종교: ${markerData[index][6]}"
        countTextView7.text = "기타 혐오: ${markerData[index][7]}"
        countTextView8.text = "악플/욕설: ${markerData[index][8]}"

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((width / 2).toFloat() * (-1), height.toFloat() * (-1))
    }
}
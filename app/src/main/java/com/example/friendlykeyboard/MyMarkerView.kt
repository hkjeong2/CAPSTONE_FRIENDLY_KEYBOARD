package com.example.friendlykeyboard

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class MyMarkerView(context: Context?, layoutResource: Int) : MarkerView(context, layoutResource) {
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
        dateTextView.text = "????-??-??"
        countTextView0.text = markerData[index][0].toString()
        countTextView1.text = markerData[index][1].toString()
        countTextView2.text = markerData[index][2].toString()
        countTextView3.text = markerData[index][3].toString()
        countTextView4.text = markerData[index][4].toString()
        countTextView5.text = markerData[index][5].toString()
        countTextView6.text = markerData[index][6].toString()
        countTextView7.text = markerData[index][7].toString()
        countTextView8.text = markerData[index][8].toString()

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((width / 2).toFloat() * (-1), height.toFloat() * (-1))
    }
}
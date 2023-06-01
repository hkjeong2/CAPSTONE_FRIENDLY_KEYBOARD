package com.example.friendlykeyboard.utils

import android.content.Context
import android.widget.TextView
import com.example.friendlykeyboard.R
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
        val countSumTextView = findViewById<TextView>(R.id.countSumTextView)

        val index = e.x.toInt()
        dateTextView.text = dateData[index]
        countTextView0.text = if (markerData[index][0] <= 99) markerData[index][0].toString() else "99+"
        countTextView1.text = if (markerData[index][1] <= 99) markerData[index][1].toString() else "99+"
        countTextView2.text = if (markerData[index][2] <= 99) markerData[index][2].toString() else "99+"
        countTextView3.text = if (markerData[index][3] <= 99) markerData[index][3].toString() else "99+"
        countTextView4.text = if (markerData[index][4] <= 99) markerData[index][4].toString() else "99+"
        countTextView5.text = if (markerData[index][5] <= 99) markerData[index][5].toString() else "99+"
        countTextView6.text = if (markerData[index][6] <= 99) markerData[index][6].toString() else "99+"
        countTextView7.text = if (markerData[index][7] <= 99) markerData[index][7].toString() else "99+"
        countTextView8.text = if (markerData[index][8] <= 99) markerData[index][8].toString() else "99+"
        countSumTextView.text = if (markerData[index].sum() <= 99) markerData[index].sum().toString() else "99+"

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((width / 2).toFloat() * (-1) - 80, height.toFloat() * (-1))
    }
}
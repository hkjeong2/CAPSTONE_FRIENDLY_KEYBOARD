package com.example.friendlykeyboard.fragments

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.friendlykeyboard.databinding.FragmentChartBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class ChartFragment : Fragment() {
    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(layoutInflater, container, false)
        initListeners()
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // 서버에서 혐오 표현 사용 횟수 데이터를 가져옴.
        val mutableList = getData()

        val lineDateSet = LineDataSet(mutableList, "혐오 표현 사용 횟수").apply {
            axisDependency = YAxis.AxisDependency.LEFT // Y값 데이터를 왼쪽으로 배치
            color = Color.BLUE
            setCircleColor(Color.BLUE) // 데이터 원형 색 지정
            valueTextSize = 10f // 값 글자 크기
            valueTextColor = Color.WHITE
            lineWidth = 2f // 라인 두께
            circleRadius = 4f // 원 크기
            fillAlpha = 0 // 라인 색 투명도
            highLightColor = Color.BLUE // 하이라이트 색깔 지정
            setDrawValues(true) // 값을 그리기
        }

        val lineData = LineData(lineDateSet)
        binding.lineChart.data = lineData

        // 범례 설정
        binding.lineChart.legend.apply {
            textSize = 15f
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(false)
        }

        // x축 설정
        binding.lineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            textColor = Color.BLUE
            granularity = 1f
            axisMinimum = 1f
            axisMaximum = 31f
            labelCount = 31
            enableGridDashedLine(10f, 20f, 0f)
        }

        // 왼쪽 y축 설정
        binding.lineChart.axisLeft.apply {
            textColor = Color.BLUE
            axisMinimum = 0f
            granularity = 10f
        }

        // 오른쪽 y축 설정
        binding.lineChart.axisRight.isEnabled = false

        with (binding.lineChart) {
            zoom(3f, 0f, 0f, 0f)
            isDoubleTapToZoomEnabled = false
            setDrawGridBackground(false)
            description.text = ""
            animateY(1000, Easing.EaseInOutCubic)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initListeners() {
        binding.button1.setOnClickListener {
            // TODO
        }

        binding.button2.setOnClickListener {
            // TODO
        }

        binding.button3.setOnClickListener {
            // TODO
        }

        binding.button4.setOnClickListener {
            // TODO
        }

        binding.button5.setOnClickListener {
            // TODO
        }

        binding.button6.setOnClickListener {
            // TODO
        }

        binding.button7.setOnClickListener {
            // TODO
        }

        binding.button8.setOnClickListener {
            // TODO
        }

        binding.button9.setOnClickListener {
            // TODO
        }

        binding.button10.setOnClickListener {
            // TODO
        }

        binding.button11.setOnClickListener {
            // TODO
        }

        binding.button12.setOnClickListener {
            // TODO
        }
    }

    // TODO: 서버에서 혐오 표현 사용 횟수 데이터를 가져옴.
    private fun getData(): MutableList<Entry> {
        val accountID = requireActivity()
            .getSharedPreferences("cbAuto", 0)
            .getString("id", "")!!
        
        // TODO: 서버에서 데이터 가져오기

        val mutableList = mutableListOf<Entry>().apply {
            add(Entry(1f, 1f))
            add(Entry(2f, 2f))
            add(Entry(3f, 0f))
            add(Entry(4f, 14f))
            add(Entry(5f, 30f))
            add(Entry(6f, 25f))
            add(Entry(7f, 50f))
            add(Entry(8f, 37f))
            add(Entry(9f, 25f))
            add(Entry(10f, 51f))
            add(Entry(11f, 22f))
            add(Entry(12f, 17f))
            add(Entry(13f, 16f))
            add(Entry(14f, 13f))
            add(Entry(15f, 20f))
            add(Entry(16f, 34f))
            add(Entry(17f, 39f))
            add(Entry(18f, 40f))
            add(Entry(19f, 35f))
            add(Entry(20f, 21f))
            add(Entry(21f, 0f))
            add(Entry(22f, 5f))
            add(Entry(23f, 4f))
            add(Entry(24f, 40f))
            add(Entry(25f, 31f))
            add(Entry(26f, 22f))
            add(Entry(27f, 25f))
            add(Entry(28f, 47f))
            add(Entry(29f, 23f))
            add(Entry(30f, 38f))
            add(Entry(31f, 24f))
        }

        return mutableList
    }
}

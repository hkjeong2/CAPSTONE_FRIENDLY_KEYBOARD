package com.example.friendlykeyboard.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.friendlykeyboard.databinding.FragmentChartBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class ChartFragment : Fragment() {
    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val mutableList = mutableListOf<Entry>().apply {
            add(Entry(1f, 1f))
            add(Entry(2f, 2f))
            add(Entry(3f, 0f))
            add(Entry(4f, 14f))
            add(Entry(5f, 30f))
            add(Entry(6f, 25f))
            add(Entry(7f, 50f))
        }

        val lineDateSet = LineDataSet(mutableList, "혐오 표현 사용 횟수").apply {
            axisDependency = YAxis.AxisDependency.LEFT // Y값 데이터를 왼쪽으로 배치
            color = Color.BLUE
            setCircleColor(Color.BLUE) // 데이터 원형 색 지정
            valueTextSize = 10f // 값 글자 크기
            valueTextColor = Color.BLUE
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
            isDoubleTapToZoomEnabled = false
            setDrawGridBackground(false)
            description.text = ""
            animateY(1000, Easing.EaseInCubic)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

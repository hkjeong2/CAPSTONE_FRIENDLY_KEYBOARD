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
            add(Entry(6f, 3f))
        }

        val lineDateSet = LineDataSet(mutableList, "혐오 표현 사용 횟수").apply {
            axisDependency = YAxis.AxisDependency.LEFT // Y값 데이터를 왼쪽으로 배치
            color = Color.GREEN
            setCircleColor(Color.BLUE) // 데이터 원형 색 지정
            valueTextSize = 10f // 값 글자 크기
            lineWidth = 2f // 라인 두께
            circleRadius = 4f // 원 크기
            fillAlpha = 0 // 라인 색 투명도
            highLightColor = Color.BLUE // 하이라이트 색깔 지정
            setDrawValues(true) // 값을 그리기
        }

        val lineData = LineData(lineDateSet)
        binding.lineChart.data = lineData

        val xAxis = binding.lineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            textColor = Color.BLUE
            enableGridDashedLine(1f, 24f, 0f)
        }

        val yLAxis = binding.lineChart.axisLeft.apply {
            textColor = Color.BLUE
            axisMinimum = 0f
        }

        val yRAxis = binding.lineChart.axisRight.apply {
            setDrawLabels(false)
            setDrawAxisLine(false)
            setDrawGridLines(false)
        }

        val description = Description().apply {
            text = ""
        }

        with (binding.lineChart) {
            isDoubleTapToZoomEnabled = false
            setDrawGridBackground(false)
            this.description = description
            animateY(1000, Easing.EaseInCubic)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

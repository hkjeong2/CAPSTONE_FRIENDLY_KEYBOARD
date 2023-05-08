package com.example.friendlykeyboard.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.friendlykeyboard.databinding.FragmentChartBinding
import com.example.friendlykeyboard.retrofit_util.Account
import com.example.friendlykeyboard.retrofit_util.RetrofitClient
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ChartFragment : Fragment() {
    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!
    private val service = RetrofitClient.getApiService()
    private lateinit var countData: MutableMap<String, MutableMap<String, Int>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(layoutInflater, container, false)
        initChartAttributes()
        initTabLayoutListener()
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // 서버에서 혐오 표현 사용 횟수 데이터를 가져옴.
        runBlocking {
            getData()
        }

        // 날짜별 혐오 표현 사용 횟수를 표시함.
        setData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initChartAttributes() {
        // Title 설정
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
            labelCount = 3
            enableGridDashedLine(10f, 20f, 0f)
        }

        // 왼쪽 y축 설정
        binding.lineChart.axisLeft.apply {
            textColor = Color.BLUE
            axisMinimum = 0f
            granularity = 10f
        }

        // 오른쪽 y축이 보이지 않도록 설정
        binding.lineChart.axisRight.isEnabled = false

        with (binding.lineChart) {
            isDoubleTapToZoomEnabled = false
            setDrawGridBackground(false)
            description.text = ""
            animateY(1000, Easing.EaseInOutCubic)
            invalidate()
        }
    }
    
    private fun initTabLayoutListener() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                setData()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    // 서버에서 혐오 표현 사용 횟수 데이터를 가져옴.
    private suspend fun getData() {
        val accountID = requireActivity()
            .getSharedPreferences("cbAuto", 0)
            .getString("id", "")!!

        val account = Account(accountID, "?")
        val counts = MutableList(9) { mapOf<String, Int>() }

        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            val response = service.getHateSpeechCounts(account)

            if (response.isSuccessful) {
                val result = response.body()!!
                counts[0] = result.count1
                counts[1] = result.count2
                counts[2] = result.count3
                counts[3] = result.count4
                counts[4] = result.count5
                counts[5] = result.count6
                counts[6] = result.count7
                counts[7] = result.count8
                counts[8] = result.count9

            } else {
                // 통신이 실패한 경우
                Log.d("ChartFragment", response.message())
                Toast.makeText(
                    requireContext(),
                    "오류가 발생하였습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        countData = mutableMapOf<String, MutableMap<String, Int>>().apply {
            for (i in counts.indices) {
                for (key in counts[i].keys) {
                    if (!containsKey(key)) {
                        put(key, mutableMapOf())
                    }

                    get(key)?.put("count${i}", counts[i][key] ?: 0)
                }
            }
        }
    }

    // TODO
    private fun setData() {
        binding.lineChart.resetZoom()

        // TODO
        val mutableList = mutableListOf<Entry>().apply {
            when (binding.tabLayout.selectedTabPosition) {
                1 -> {
                    // 30일
                    binding.lineChart.zoom(3f, 0f, 0f, 0f)
                }
                2 -> {
                    // 90일
                }
                3 -> {
                    // 1년
                }
                4 -> {
                    // 모든 시간
                }
                else -> {
                    // 7일
                    binding.lineChart.zoom(3f, 0f, 0f, 0f)
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
            }
        }

        val lineDateSet = LineDataSet(mutableList, "혐오 표현 사용 횟수").apply {
            axisDependency = YAxis.AxisDependency.LEFT // Y값 데이터를 왼쪽으로 배치
            color = Color.BLUE
            setCircleColor(Color.BLUE) // 데이터 원형 색 지정
            valueTextSize = 15f // 값 글자 크기
            valueTextColor = Color.WHITE
            lineWidth = 2f // 라인 두께
            circleRadius = 4f // 원 크기
            fillAlpha = 0 // 라인 색 투명도
            highLightColor = Color.BLUE // 하이라이트 색깔 지정
            setDrawValues(true) // 값을 그리기
        }

        val lineData = LineData(lineDateSet)
        binding.lineChart.data = lineData
    }
}

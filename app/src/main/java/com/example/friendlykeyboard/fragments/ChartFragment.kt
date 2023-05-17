package com.example.friendlykeyboard.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.friendlykeyboard.MyMarkerView
import com.example.friendlykeyboard.R
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
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ChartFragment : Fragment() {
    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!
    private val service = RetrofitClient.getApiService()
    private lateinit var countData: MutableMap<String, MutableMap<String, Int>>
    private lateinit var labels: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(layoutInflater, container, false)
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
            textColor = Color.BLACK
            axisMinimum = 0f
            axisMaximum = (labels.size - 1).toFloat()
            granularity = 1f
            labelCount = labels.size
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return ""
                }
            }
            enableGridDashedLine(10f, 20f, 0f)
        }

        // 왼쪽 y축 설정
        binding.lineChart.axisLeft.apply {
            textColor = Color.BLACK
            axisMinimum = 0f
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
            try {
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
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            requireContext(),
                            "오류가 발생하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.d("ChartFragment", "Connection Error")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        requireContext(),
                        "서버와의 통신이 실패하였습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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

    private fun setData() {
        binding.lineChart.fitScreen()
        labels = mutableListOf()
        val markerData = mutableListOf<IntArray>()

        val mutableList = mutableListOf<Entry>().apply {
            when (binding.tabLayout.selectedTabPosition) {
                3 -> {
                    // 모든 시간
                    var x = 0f
                    for (key in countData.keys) {
                        labels.add(key)
                        val arr = IntArray(9) { 0 }
                        var sum = 0
                        for (i in 0..8) {
                            sum += countData[key]?.get("count$i") ?: 0
                            arr[i] = countData[key]?.get("count$i") ?: 0
                        }
                        add(Entry(x, sum.toFloat()))
                        markerData.add(arr)
                        x++
                    }
                }
                else -> {
                    val period = when (binding.tabLayout.selectedTabPosition) {
                        0 -> 7L
                        1 -> 30L
                        else -> 356L
                    }

                    val currentDate = System.currentTimeMillis()
                    var x = 0f
                    for (key in countData.keys) {
                        val date = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN).parse(key)?.time ?: 0L
                        val diffSec = (currentDate - date) / 1000
                        val diffDays = diffSec / (24 * 60 * 60)
                        if (diffDays < period) {
                            labels.add(key)
                            val arr = IntArray(9) { 0 }
                            var sum = 0
                            for (i in 0..8) {
                                sum += countData[key]?.get("count$i") ?: 0
                                arr[i] = countData[key]?.get("count$i") ?: 0
                            }
                            add(Entry(x, sum.toFloat()))
                            markerData.add(arr)
                            x++
                        }
                    }
                }
            }
        }

        initChartAttributes()

        val lineDateSet = LineDataSet(mutableList, "혐오표현 사용 횟수").apply {
            axisDependency = YAxis.AxisDependency.LEFT // Y값 데이터를 왼쪽으로 배치
            color = resources.getColor(R.color.background_blue)
            setCircleColor(resources.getColor(R.color.background_blue)) // 데이터 원형 색 지정
            valueTextSize = 15f // 값 글자 크기
            valueTextColor = Color.BLACK
            lineWidth = 2f // 라인 두께
            circleRadius = 5f // 원 크기
            circleHoleRadius = 3f
            highLightColor = Color.BLACK // 하이라이트 색깔 지정
            fillColor = resources.getColor(R.color.background_blue)
            setDrawFilled(true)
            setDrawValues(true) // 값을 그리기
        }

        val lineData = LineData(lineDateSet)
        binding.lineChart.data = lineData

        val markerView = MyMarkerView(context, R.layout.mymarkerview)
        markerView.dateData = labels.toTypedArray()
        markerView.markerData = markerData.toTypedArray()
        binding.lineChart.marker = markerView
    }
}

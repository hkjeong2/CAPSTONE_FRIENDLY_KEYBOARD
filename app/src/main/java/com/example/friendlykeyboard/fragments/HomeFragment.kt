package com.example.friendlykeyboard.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.friendlykeyboard.databinding.FragmentHomeBinding
import com.example.friendlykeyboard.retrofit_util.Account
import com.example.friendlykeyboard.retrofit_util.RetrofitClient
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val service = RetrofitClient.getApiService()
    private lateinit var labels: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        labels = mutableListOf<String>().apply {
            add("여성/가족")
            add("남성")
            add("성소수자")
            add("인종/국적")
            add("연령")
            add("지역")
            add("종교")
            add("기타 혐오")
            add("악플/욕설")
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // 서버에서 혐오 표현 사용 횟수를 가져와서 표시.
        runBlocking {
            getAndSetData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initChartAttributes() {
        // Title 설정
        binding.barChart.legend.isEnabled = false

        // x축 설정
        binding.barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            textSize = 10f
            granularity = 1f
            labelCount = labels.size
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return labels[value.toInt()]
                }
            }
        }

        // 왼쪽 y축 설정
        binding.barChart.axisLeft.apply {
            textColor = Color.BLUE
            axisMinimum = 0f
        }

        // 오른쪽 y축 설정
        binding.barChart.axisRight.apply {
            textColor = Color.BLUE
            axisMinimum = 0f
        }

        with (binding.barChart) {
            description.text = ""
            animateY(1000, Easing.EaseInOutCubic)
        }
    }

    private suspend fun getAndSetData() {
        val accountID = requireActivity()
            .getSharedPreferences("cbAuto", 0)
            .getString("id", "")!!

        binding.idTextView.text = accountID

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

        var sum = 0
        val mutableList = mutableListOf<BarEntry>().apply {
            for (index in counts.indices) {
                var countSum = 0
                for (key in counts[index].keys) {
                    countSum += counts[index][key] ?: 0
                    sum += counts[index][key] ?: 0
                }
                add(BarEntry(index.toFloat(), countSum.toFloat()))
            }
        }

        binding.countTextView.text = sum.toString()

        initChartAttributes()

        val barDataSet = BarDataSet(mutableList, "혐오표현 전체 사용 횟수").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 15f // 값 글자 크기
            valueTextColor = Color.RED
        }

        val barData = BarData(barDataSet)
        binding.barChart.data = barData
    }
}
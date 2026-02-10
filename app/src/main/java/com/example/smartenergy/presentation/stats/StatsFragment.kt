package com.example.smartenergy.presentation.stats

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.smartenergy.R
import com.example.smartenergy.databinding.FragmentStatsBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatsFragment : Fragment(R.layout.fragment_stats) {
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val vm: StatsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentStatsBinding.bind(view)
        vm.loadWeekly()
        viewLifecycleOwner.lifecycleScope.launch {
            vm.state.collect { binding.tvWeekly.text = it }
        }
        val entries = listOf(Entry(1f, 120f), Entry(2f, 80f), Entry(3f, 90f), Entry(4f, 60f), Entry(5f, 130f))
        val ds = LineDataSet(entries, "W trend").apply { color = Color.BLUE; valueTextColor = Color.DKGRAY }
        binding.chart.data = LineData(ds)
        binding.chart.invalidate()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

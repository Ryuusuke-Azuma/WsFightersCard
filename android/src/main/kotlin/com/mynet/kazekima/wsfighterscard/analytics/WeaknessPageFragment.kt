/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.analytics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.mynet.kazekima.wsfighterscard.analytics.models.OpponentLossStat
import com.mynet.kazekima.wsfighterscard.databinding.PageAnalyticsWeaknessBinding

class WeaknessPageFragment : Fragment() {
    private val analyticsViewModel: AnalyticsViewModel by activityViewModels()
    private val weaknessViewModel: WeaknessViewModel by activityViewModels()
    private var _binding: PageAnalyticsWeaknessBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = PageAnalyticsWeaknessBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        weaknessViewModel.opponentLossStats.observe(viewLifecycleOwner) { stats ->
            setupLossPieChart(_binding!!.chartLossDistribution, stats)
        }

        analyticsViewModel.startDate.observe(viewLifecycleOwner) { startDate ->
            val endDate = analyticsViewModel.endDate.value
            if (endDate != null) {
                weaknessViewModel.loadOpponentStats(startDate, endDate)
            }
        }

        analyticsViewModel.endDate.observe(viewLifecycleOwner) { endDate ->
            val startDate = analyticsViewModel.startDate.value
            if (startDate != null) {
                weaknessViewModel.loadOpponentStats(startDate, endDate)
            }
        }
    }

    private fun setupLossPieChart(chart: PieChart, data: List<OpponentLossStat>) {
        if (data.isEmpty()) { chart.clear(); return }
        val entries = data.map { PieEntry(it.lossCount.toFloat(), it.deckName) }
        val dataSet = PieDataSet(entries, "Losses by Opponent").apply {
            colors = ColorTemplate.COLORFUL_COLORS.toList()
            valueTextSize = 12f
            valueTextColor = Color.BLACK
            sliceSpace = 3f
        }
        chart.data = PieData(dataSet).apply { setValueFormatter(DefaultValueFormatter(0)) }
        chart.setUsePercentValues(false)
        chart.description.isEnabled = false
        chart.legend.isWordWrapEnabled = true
        chart.isRotationEnabled = false
        chart.animateXY(1000, 1000)
        chart.invalidate()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

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
import com.github.mikephil.charting.formatter.PercentFormatter
import com.mynet.kazekima.wsfighterscard.analytics.models.DetailedWinLose
import com.mynet.kazekima.wsfighterscard.databinding.PageAnalyticsSummaryBinding

class SummaryPageFragment : Fragment() {
    private val analyticsViewModel: AnalyticsViewModel by activityViewModels()
    private val summaryViewModel: SummaryViewModel by activityViewModels()
    private var _binding: PageAnalyticsSummaryBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = PageAnalyticsSummaryBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        summaryViewModel.totalGameCount.observe(viewLifecycleOwner) { _binding!!.textTotalGames.text = it.toString() }

        summaryViewModel.individualWinLose.observe(viewLifecycleOwner) { winLose ->
            _binding!!.textTotalWins.text = winLose.first.toString()
            _binding!!.textTotalLosses.text = winLose.second.toString()
            setupCommonInnerPieChart(_binding!!.chartIndividualInner, winLose)
        }
        summaryViewModel.detailedWinLose.observe(viewLifecycleOwner) { detail ->
            setupDetailedOuterPieChart(_binding!!.chartIndividualOuter, detail)
        }
        summaryViewModel.teamsWinLose.observe(viewLifecycleOwner) { teamWinLose ->
            setupCommonInnerPieChart(_binding!!.chartTeamsInner, teamWinLose)
        }
        summaryViewModel.detailedWinLose.observe(viewLifecycleOwner) { detail ->
            setupTeamsPersonalOuterPieChart(_binding!!.chartTeamsOuter, detail)
        }

        analyticsViewModel.startDate.observe(viewLifecycleOwner) { startDate ->
            val endDate = analyticsViewModel.endDate.value
            if (endDate != null) {
                summaryViewModel.loadSummaryStats(startDate, endDate)
            }
        }

        analyticsViewModel.endDate.observe(viewLifecycleOwner) { endDate ->
            val startDate = analyticsViewModel.startDate.value
            if (startDate != null) {
                summaryViewModel.loadSummaryStats(startDate, endDate)
            }
        }
    }

    private fun setupCommonInnerPieChart(chart: PieChart, winLose: Pair<Int, Int>) {
        if (winLose.first == 0 && winLose.second == 0) { chart.clear(); return }
        val entries = listOf(PieEntry(winLose.first.toFloat(), ""), PieEntry(winLose.second.toFloat(), ""))
        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(Color.rgb(33, 150, 243), Color.rgb(244, 67, 54))
            valueTextSize = 11f
            valueTextColor = Color.WHITE
        }
        chart.data = PieData(dataSet).apply { setValueFormatter(PercentFormatter(chart)) }
        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.isRotationEnabled = false
        chart.setTouchEnabled(false)
        chart.setHoleColor(Color.TRANSPARENT)
        chart.holeRadius = 0f
        chart.transparentCircleRadius = 0f
        chart.minOffset = 60f
        chart.invalidate()
    }

    private fun setupDetailedOuterPieChart(chart: PieChart, detail: DetailedWinLose) {
        val entries = listOf(
            PieEntry(detail.singlesWins.toFloat(), "S-W"),
            PieEntry(detail.teamsPersonalWins.toFloat(), "T-W"),
            PieEntry(detail.singlesLosses.toFloat(), "S-L"),
            PieEntry(detail.teamsPersonalLosses.toFloat(), "T-L")
        ).filter { it.value > 0 }

        if (entries.isEmpty()) { chart.clear(); return }

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(Color.rgb(100, 181, 246), Color.rgb(25, 118, 210), Color.rgb(229, 115, 115), Color.rgb(198, 40, 40))
            valueTextSize = 9f
            valueTextColor = Color.WHITE
            sliceSpace = 2f
        }
        chart.data = PieData(dataSet).apply { setValueFormatter(DefaultValueFormatter(0)) }
        chart.setUsePercentValues(false)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.isRotationEnabled = false
        chart.minOffset = 0f
        chart.holeRadius = 75f
        chart.setDrawEntryLabels(true)
        chart.setEntryLabelTextSize(7f)
        chart.invalidate()
    }

    private fun setupTeamsPersonalOuterPieChart(chart: PieChart, detail: DetailedWinLose) {
        val entries = listOf(
            PieEntry(detail.teamsPersonalWins.toFloat(), "P-W"),
            PieEntry(detail.teamsPersonalLosses.toFloat(), "P-L")
        ).filter { it.value > 0 }

        if (entries.isEmpty()) { chart.clear(); return }

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(Color.rgb(156, 39, 176), Color.rgb(255, 152, 0))
            valueTextSize = 9f
            valueTextColor = Color.WHITE
            sliceSpace = 2f
        }
        chart.data = PieData(dataSet).apply { setValueFormatter(PercentFormatter(chart)) }
        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.isRotationEnabled = false
        chart.minOffset = 0f
        chart.holeRadius = 75f
        chart.setDrawEntryLabels(true)
        chart.setEntryLabelTextSize(7f)
        chart.invalidate()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

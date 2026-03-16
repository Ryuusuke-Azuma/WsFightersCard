/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.analytics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.analytics.models.DetailedWinLose
import com.mynet.kazekima.wsfighterscard.databinding.PageAnalyticsSummaryBinding

class SummaryPageFragment : Fragment() {
    private val analyticsViewModel: AnalyticsViewModel by activityViewModels()
    private val summaryViewModel: SummaryViewModel by activityViewModels()
    private var _binding: PageAnalyticsSummaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = PageAnalyticsSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        summaryViewModel.totalGameCount.observe(viewLifecycleOwner) { binding.textSummaryTotalGames.text = it.toString() }

        summaryViewModel.individualWinLose.observe(viewLifecycleOwner) { winLose ->
            binding.textSummaryTotalWins.text = winLose.first.toString()
            binding.textSummaryTotalLosses.text = winLose.second.toString()
            setupIndividualInnerPieChart(binding.chartSummaryIndividualInner, winLose)
        }
        summaryViewModel.detailedWinLose.observe(viewLifecycleOwner) { detail ->
            setupDetailedOuterPieChart(binding.chartSummaryIndividualOuter, detail)
        }
        summaryViewModel.teamsWinLose.observe(viewLifecycleOwner) { teamWinLose ->
            setupTeamsInnerPieChart(binding.chartSummaryTeamsInner, teamWinLose)
        }
        summaryViewModel.detailedWinLose.observe(viewLifecycleOwner) { detail ->
            setupTeamsPersonalOuterPieChart(binding.chartSummaryTeamsOuter, detail)
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

    private fun setupIndividualInnerPieChart(chart: PieChart, winLose: Pair<Int, Int>) {
        if (winLose.first == 0 && winLose.second == 0) { chart.clear(); return }
        val entries = listOf(PieEntry(winLose.first.toFloat(), ""), PieEntry(winLose.second.toFloat(), ""))
        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                ContextCompat.getColor(requireContext(), R.color.result_win_blue),
                ContextCompat.getColor(requireContext(), R.color.result_lose_red)
            )
            valueTextSize = 14f
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

    private fun setupTeamsInnerPieChart(chart: PieChart, winLose: Pair<Int, Int>) {
        if (winLose.first == 0 && winLose.second == 0) { chart.clear(); return }
        val entries = listOf(PieEntry(winLose.first.toFloat(), ""), PieEntry(winLose.second.toFloat(), ""))
        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                ContextCompat.getColor(requireContext(), R.color.result_team_win_purple),
                ContextCompat.getColor(requireContext(), R.color.result_team_lose_orange)
            )
            valueTextSize = 14f
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
            // Colors: Light Blue, Dark Blue, Light Red, Dark Red (Adjusted for contrast)
            colors = listOf(
                Color.rgb(129, 212, 250), // Light Blue
                Color.rgb(2, 136, 209),   // Dark Blue
                Color.rgb(239, 154, 154), // Light Red
                Color.rgb(211, 47, 47)    // Dark Red
            )
            valueTextSize = 14f
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
        chart.setEntryLabelTextSize(14f)
        chart.invalidate()
    }

    private fun setupTeamsPersonalOuterPieChart(chart: PieChart, detail: DetailedWinLose) {
        val entries = listOf(
            PieEntry(detail.teamsPersonalWins.toFloat(), "P-W"),
            PieEntry(detail.teamsPersonalLosses.toFloat(), "P-L")
        ).filter { it.value > 0 }

        if (entries.isEmpty()) { chart.clear(); return }

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                ContextCompat.getColor(requireContext(), R.color.result_win_blue),
                ContextCompat.getColor(requireContext(), R.color.result_lose_red)
            )
            valueTextSize = 14f
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
        chart.setEntryLabelTextSize(14f)
        chart.invalidate()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

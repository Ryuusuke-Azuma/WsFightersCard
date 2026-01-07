/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.analytics

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.tabs.TabLayoutMediator
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.analytics.models.DeckStat
import com.mynet.kazekima.wsfighterscard.analytics.models.DetailedWinLose
import com.mynet.kazekima.wsfighterscard.analytics.models.OpponentLossStat
import com.mynet.kazekima.wsfighterscard.databinding.PageAnalyticsDecksBinding
import com.mynet.kazekima.wsfighterscard.databinding.PageAnalyticsSummaryBinding
import com.mynet.kazekima.wsfighterscard.databinding.PageAnalyticsWeaknessBinding
import com.mynet.kazekima.wsfighterscard.databinding.FragmentAnalyticsBinding
import com.mynet.kazekima.wsfighterscard.databinding.ListitemDeckStatBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AnalyticsViewModel by viewModels()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDateRangeSelectors()

        val adapter = AnalyticsPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.analytics_tab_summary)
                1 -> getString(R.string.analytics_tab_my_decks)
                2 -> getString(R.string.analytics_tab_weaknesses)
                else -> ""
            }
        }.attach()

        viewModel.loadStats()
    }

    private fun setupDateRangeSelectors() {
        viewModel.startDate.observe(viewLifecycleOwner) { date ->
            binding.btnStartDate.text = date.format(dateFormatter)
        }
        viewModel.endDate.observe(viewLifecycleOwner) { date ->
            binding.btnEndDate.text = date.format(dateFormatter)
        }

        binding.btnStartDate.setOnClickListener { showDatePicker(true) }
        binding.btnEndDate.setOnClickListener { showDatePicker(false) }
    }

    private fun showDatePicker(isStart: Boolean) {
        val current = if (isStart) viewModel.startDate.value!! else viewModel.endDate.value!!
        DatePickerDialog(requireContext(), { _, year, month, day ->
            val selected = LocalDate.of(year, month + 1, day)
            if (isStart) {
                viewModel.setDateRange(selected, viewModel.endDate.value!!)
            } else {
                viewModel.setDateRange(viewModel.startDate.value!!, selected)
            }
        }, current.year, current.monthValue - 1, current.dayOfMonth).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class AnalyticsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3
        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> SummaryPageFragment()
            1 -> DecksPageFragment()
            2 -> WeaknessPageFragment()
            else -> throw IllegalArgumentException()
        }
    }

    class SummaryPageFragment : Fragment() {
        private val viewModel: AnalyticsViewModel by viewModels({ requireParentFragment() })
        private var _binding: PageAnalyticsSummaryBinding? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            _binding = PageAnalyticsSummaryBinding.inflate(inflater, container, false)
            return _binding!!.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            viewModel.totalGameCount.observe(viewLifecycleOwner) { _binding!!.textTotalGames.text = it.toString() }
            
            viewModel.individualWinLose.observe(viewLifecycleOwner) { winLose ->
                _binding!!.textTotalWins.text = winLose.first.toString()
                _binding!!.textTotalLosses.text = winLose.second.toString()
                setupCommonInnerPieChart(_binding!!.chartIndividualInner, winLose)
            }
            viewModel.detailedWinLose.observe(viewLifecycleOwner) { detail ->
                setupDetailedOuterPieChart(_binding!!.chartIndividualOuter, detail)
            }

            viewModel.teamsWinLose.observe(viewLifecycleOwner) { teamWinLose ->
                setupCommonInnerPieChart(_binding!!.chartTeamsInner, teamWinLose)
            }
            viewModel.detailedWinLose.observe(viewLifecycleOwner) { detail ->
                setupTeamsPersonalOuterPieChart(_binding!!.chartTeamsOuter, detail)
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

    class DecksPageFragment : Fragment() {
        private val viewModel: AnalyticsViewModel by viewModels({ requireParentFragment() })
        private var _binding: PageAnalyticsDecksBinding? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            _binding = PageAnalyticsDecksBinding.inflate(inflater, container, false)
            return _binding!!.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val adapter = DeckStatsAdapter()
            _binding!!.recyclerDeckStats.adapter = adapter
            viewModel.deckStats.observe(viewLifecycleOwner) { adapter.submitList(it) }
        }

        override fun onDestroyView() { super.onDestroyView(); _binding = null }
    }

    class WeaknessPageFragment : Fragment() {
        private val viewModel: AnalyticsViewModel by viewModels({ requireParentFragment() })
        private var _binding: PageAnalyticsWeaknessBinding? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            _binding = PageAnalyticsWeaknessBinding.inflate(inflater, container, false)
            return _binding!!.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            viewModel.opponentLossStats.observe(viewLifecycleOwner) { stats ->
                setupLossPieChart(_binding!!.chartLossDistribution, stats)
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

    private class DeckStatsAdapter : ListAdapter<DeckStat, DeckStatsAdapter.ViewHolder>(DiffCallback) {
        class ViewHolder(val binding: ListitemDeckStatBinding) : RecyclerView.ViewHolder(binding.root)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(ListitemDeckStatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)
            with(holder.binding) {
                textDeckName.text = item.deckName
                textStatsDetail.text = root.context.getString(R.string.format_deck_stats_detail, item.totalGames, item.winCount, item.lossCount)
                textWinRate.text = root.context.getString(R.string.format_percentage, item.winRate)
            }
        }
        companion object {
            private val DiffCallback = object : DiffUtil.ItemCallback<DeckStat>() {
                override fun areItemsTheSame(old: DeckStat, new: DeckStat) = old.deckName == new.deckName
                override fun areContentsTheSame(old: DeckStat, new: DeckStat) = old == new
            }
        }
    }
}

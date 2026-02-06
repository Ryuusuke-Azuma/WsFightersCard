/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.analytics

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.FragmentAnalyticsBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!
    private val analyticsViewModel: AnalyticsViewModel by activityViewModels()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDateRangeSelectors()

        binding.pagerAnalytics.adapter = AnalyticsPagerAdapter(this)

        TabLayoutMediator(binding.tabsAnalytics, binding.pagerAnalytics) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.analytics_tab_summary)
                1 -> getString(R.string.analytics_tab_strengths)
                2 -> getString(R.string.analytics_tab_weaknesses)
                else -> ""
            }
        }.attach()
    }

    private fun setupDateRangeSelectors() {
        analyticsViewModel.startDate.observe(viewLifecycleOwner) { date ->
            binding.buttonAnalyticsStartDate.text = date.format(dateFormatter)
        }
        analyticsViewModel.endDate.observe(viewLifecycleOwner) { date ->
            binding.buttonAnalyticsEndDate.text = date.format(dateFormatter)
        }

        binding.buttonAnalyticsStartDate.setOnClickListener { showDatePicker(true) }
        binding.buttonAnalyticsEndDate.setOnClickListener { showDatePicker(false) }
    }

    private fun showDatePicker(isStart: Boolean) {
        val current = if (isStart) analyticsViewModel.startDate.value!! else analyticsViewModel.endDate.value!!
        DatePickerDialog(requireContext(), { _, year, month, day ->
            val selected = LocalDate.of(year, month + 1, day)
            if (isStart) {
                analyticsViewModel.setDateRange(selected, analyticsViewModel.endDate.value!!)
            } else {
                analyticsViewModel.setDateRange(analyticsViewModel.startDate.value!!, selected)
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
            1 -> StrengthsPageFragment()
            2 -> WeaknessPageFragment()
            else -> throw IllegalArgumentException()
        }
    }
}

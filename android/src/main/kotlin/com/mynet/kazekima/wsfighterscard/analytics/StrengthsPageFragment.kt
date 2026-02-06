/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.analytics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.analytics.models.StrengthStat
import com.mynet.kazekima.wsfighterscard.databinding.ListitemStrengthBinding
import com.mynet.kazekima.wsfighterscard.databinding.PageAnalyticsStrengthsBinding

class StrengthsPageFragment : Fragment() {
    private val analyticsViewModel: AnalyticsViewModel by activityViewModels()
    private val strengthsViewModel: StrengthsViewModel by activityViewModels()
    private var _binding: PageAnalyticsStrengthsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = PageAnalyticsStrengthsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = StrengthStatsAdapter()
        binding.recyclerAnalyticsStrengths.adapter = adapter

        strengthsViewModel.strengthStats.observe(viewLifecycleOwner) { adapter.submitList(it) }

        analyticsViewModel.startDate.observe(viewLifecycleOwner) { startDate ->
            val endDate = analyticsViewModel.endDate.value
            if (endDate != null) {
                strengthsViewModel.loadStrengthStats(startDate, endDate)
            }
        }

        analyticsViewModel.endDate.observe(viewLifecycleOwner) { endDate ->
            val startDate = analyticsViewModel.startDate.value
            if (startDate != null) {
                strengthsViewModel.loadStrengthStats(startDate, endDate)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class StrengthStatsAdapter : ListAdapter<StrengthStat, StrengthStatsAdapter.ViewHolder>(DiffCallback) {
        class ViewHolder(val binding: ListitemStrengthBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ListitemStrengthBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)
            with(holder.binding) {
                textStrengthDeckName.text = item.deckName
                textStrengthStatsDetail.text = root.context.getString(R.string.analytics_format_deck_stats_detail, item.totalGames, item.winCount, item.lossCount)
                textStrengthWinRate.text = root.context.getString(R.string.analytics_format_percentage, item.winRate)
            }
        }

        companion object {
            private val DiffCallback = object : DiffUtil.ItemCallback<StrengthStat>() {
                override fun areItemsTheSame(old: StrengthStat, new: StrengthStat): Boolean {
                    return old.deckName == new.deckName
                }

                override fun areContentsTheSame(old: StrengthStat, new: StrengthStat): Boolean {
                    return old == new
                }
            }
        }
    }
}

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
import com.mynet.kazekima.wsfighterscard.analytics.models.DeckStat
import com.mynet.kazekima.wsfighterscard.databinding.ListitemDeckStatBinding
import com.mynet.kazekima.wsfighterscard.databinding.PageAnalyticsDecksBinding

class DecksPageFragment : Fragment() {
    private val analyticsViewModel: AnalyticsViewModel by activityViewModels()
    private val decksViewModel: DecksViewModel by activityViewModels()
    private var _binding: PageAnalyticsDecksBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = PageAnalyticsDecksBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = DeckStatsAdapter()
        _binding!!.recyclerDeckStats.adapter = adapter

        decksViewModel.deckStats.observe(viewLifecycleOwner) { adapter.submitList(it) }

        analyticsViewModel.startDate.observe(viewLifecycleOwner) { startDate ->
            val endDate = analyticsViewModel.endDate.value
            if (endDate != null) {
                decksViewModel.loadDeckStats(startDate, endDate)
            }
        }

        analyticsViewModel.endDate.observe(viewLifecycleOwner) { endDate ->
            val startDate = analyticsViewModel.startDate.value
            if (startDate != null) {
                decksViewModel.loadDeckStats(startDate, endDate)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class DeckStatsAdapter : ListAdapter<DeckStat, DeckStatsAdapter.ViewHolder>(DiffCallback) {
        class ViewHolder(val binding: ListitemDeckStatBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ListitemDeckStatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

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
                override fun areItemsTheSame(old: DeckStat, new: DeckStat): Boolean {
                    return old.deckName == new.deckName
                }

                override fun areContentsTheSame(old: DeckStat, new: DeckStat): Boolean {
                    return old == new
                }
            }
        }
    }
}

/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mynet.kazekima.wsfighterscard.databinding.ListitemGameBinding
import com.mynet.kazekima.wsfighterscard.db.SelectGamesWithStatsByDate
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ScheduleListAdapter(
    private val onItemClick: (SelectGamesWithStatsByDate) -> Unit
) : ListAdapter<SelectGamesWithStatsByDate, ScheduleListAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ListitemGameBinding) : RecyclerView.ViewHolder(binding.root) {
        private val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

        fun bind(item: SelectGamesWithStatsByDate, onItemClick: (SelectGamesWithStatsByDate) -> Unit) {
            binding.itemTitle.text = item.game_name
            
            val date = Instant.ofEpochMilli(item.game_date).atZone(ZoneId.systemDefault()).toLocalDate()
            binding.itemDate.text = date.format(formatter)
            
            binding.itemStats.text = "${item.win_count}W ${item.loss_count}L"
            
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListitemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<SelectGamesWithStatsByDate>() {
            override fun areItemsTheSame(oldItem: SelectGamesWithStatsByDate, newItem: SelectGamesWithStatsByDate): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: SelectGamesWithStatsByDate, newItem: SelectGamesWithStatsByDate): Boolean = oldItem == newItem
        }
    }
}

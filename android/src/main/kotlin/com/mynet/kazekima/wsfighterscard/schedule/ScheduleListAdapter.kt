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
import com.mynet.kazekima.wsfighterscard.schedule.models.GameDisplayItem
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ScheduleListAdapter(
    private val onItemClick: (GameDisplayItem) -> Unit,
    private val onMoreClick: (view: android.view.View, item: GameDisplayItem) -> Unit
) : ListAdapter<GameDisplayItem, ScheduleListAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ListitemGameBinding) : RecyclerView.ViewHolder(binding.root) {
        private val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

        fun bind(
            item: GameDisplayItem, 
            onItemClick: (GameDisplayItem) -> Unit,
            onMoreClick: (android.view.View, GameDisplayItem) -> Unit
        ) {
            val game = item.game
            binding.itemTitle.text = game.game_name
            
            val date = Instant.ofEpochMilli(game.game_date).atZone(ZoneId.systemDefault()).toLocalDate()
            binding.itemDate.text = date.format(formatter)
            
            binding.itemStats.text = "${item.winCount}W ${item.lossCount}L"
            
            binding.root.setOnClickListener { onItemClick(item) }
            binding.btnMore.setOnClickListener { onMoreClick(it, item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListitemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick, onMoreClick)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<GameDisplayItem>() {
            override fun areItemsTheSame(oldItem: GameDisplayItem, newItem: GameDisplayItem): Boolean = oldItem.game.id == newItem.game.id
            override fun areContentsTheSame(oldItem: GameDisplayItem, newItem: GameDisplayItem): Boolean = oldItem == newItem
        }
    }
}

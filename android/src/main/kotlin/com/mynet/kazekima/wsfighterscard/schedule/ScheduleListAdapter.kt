/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mynet.kazekima.wsfighterscard.databinding.ScheduleItemBinding
import com.mynet.kazekima.wsfighterscard.db.Game

/**
 * スケジュール一覧を表示するための RecyclerView アダプター
 */
class ScheduleListAdapter(
    private val onItemClick: (Game) -> Unit
) : ListAdapter<Game, ScheduleListAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ScheduleItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(game: Game, onItemClick: (Game) -> Unit) {
            binding.itemTitle.text = game.game_name
            binding.itemDate.text = game.game_date
            
            binding.root.setOnClickListener {
                onItemClick(game)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ScheduleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Game>() {
            override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
                return oldItem == newItem
            }
        }
    }
}

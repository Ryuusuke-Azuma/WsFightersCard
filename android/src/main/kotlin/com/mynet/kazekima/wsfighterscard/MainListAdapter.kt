/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.mynet.kazekima.wsfighterscard.databinding.ContentItemBinding
import com.mynet.kazekima.wsfighterscard.db.Game

class MainListAdapter(private val context: Context) : BaseAdapter() {
    private var games: List<Game> = emptyList()

    fun updateData(newGames: List<Game>) {
        this.games = newGames
        notifyDataSetChanged()
    }

    override fun getCount(): Int = games.size

    override fun getItem(position: Int): Game = games[position]

    override fun getItemId(position: Int): Long = games[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ContentItemBinding
        val view: View

        if (convertView == null) {
            binding = ContentItemBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ContentItemBinding
        }

        val game = getItem(position)
        binding.itemTitle.text = game.game_name
        binding.itemDate.text = game.game_date

        return view
    }
}

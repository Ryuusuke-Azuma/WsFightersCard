/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule

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
import com.mynet.kazekima.wsfighterscard.databinding.ListitemGameBinding
import com.mynet.kazekima.wsfighterscard.databinding.PageScheduleGamesBinding
import com.mynet.kazekima.wsfighterscard.schedule.models.GameDisplayItem
import com.mynet.kazekima.wsfighterscard.schedule.record.DeleteGameDialogFragment
import com.mynet.kazekima.wsfighterscard.schedule.record.RecordGameDialogFragment
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GamesPageFragment : Fragment() {
    private val scheduleViewModel: ScheduleViewModel by activityViewModels()
    private val gamesViewModel: GamesViewModel by activityViewModels()
    private var _binding: PageScheduleGamesBinding? = null
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = PageScheduleGamesBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = GamesListAdapter(
            onItemClick = { item ->
                gamesViewModel.selectGame(item)
            },
            onMoreClick = { item -> showScheduleBottomSheet(item) }
        )
        _binding!!.recyclerViewGames.adapter = adapter
        gamesViewModel.games.observe(viewLifecycleOwner) { adapter.submitList(it) }

        scheduleViewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            gamesViewModel.loadGamesForDate(date)
        }

        childFragmentManager.setFragmentResultListener(RecordGameDialogFragment.REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            if (bundle.getBoolean(RecordGameDialogFragment.RESULT_SAVED)) {
                scheduleViewModel.selectedDate.value?.let { gamesViewModel.loadGamesForDate(it) }
                scheduleViewModel.loadData()
            }
        }
        childFragmentManager.setFragmentResultListener(DeleteGameDialogFragment.REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            if (bundle.getBoolean(DeleteGameDialogFragment.RESULT_DELETED)) {
                scheduleViewModel.selectedDate.value?.let { gamesViewModel.loadGamesForDate(it) }
                scheduleViewModel.loadData()
            }
        }
        childFragmentManager.setFragmentResultListener(ScheduleBottomSheet.REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            val result = bundle.getString(ScheduleBottomSheet.RESULT_KEY)
            val itemId = bundle.getLong(ScheduleBottomSheet.ITEM_ID)
            val item = gamesViewModel.games.value?.find { it.game.id == itemId } ?: return@setFragmentResultListener

            when (result) {
                ScheduleBottomSheet.ACTION_EDIT -> {
                    val dateStr = Instant.ofEpochMilli(item.game.game_date).atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter)
                    RecordGameDialogFragment.newInstanceForEdit(item.game.id, item.game.game_name, dateStr, item.game.game_style.id, item.game.memo)
                        .show(childFragmentManager, RecordGameDialogFragment.REQUEST_KEY)
                }
                ScheduleBottomSheet.ACTION_DELETE -> {
                    DeleteGameDialogFragment.newInstance(item.game.id, item.game.game_name)
                        .show(childFragmentManager, DeleteGameDialogFragment.REQUEST_KEY)
                }
            }
        }
    }

    fun showAddDialog() {
        val date = scheduleViewModel.selectedDate.value ?: return
        RecordGameDialogFragment.newInstance(date.format(dateFormatter))
            .show(childFragmentManager, RecordGameDialogFragment.REQUEST_KEY)
    }

    private fun showScheduleBottomSheet(item: GameDisplayItem) {
        ScheduleBottomSheet.newInstance(item.game.id)
            .show(childFragmentManager, ScheduleBottomSheet.REQUEST_KEY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class GamesListAdapter(
        private val onItemClick: (GameDisplayItem) -> Unit,
        private val onMoreClick: (item: GameDisplayItem) -> Unit
    ) : ListAdapter<GameDisplayItem, GamesListAdapter.ViewHolder>(DiffCallback) {

        class ViewHolder(val binding: ListitemGameBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ListitemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)
            val game = item.game
            val context = holder.binding.root.context
            with(holder.binding) {
                listHeader.headerText.text = game.game_style.label
                itemTitle.text = game.game_name
                itemMemo.text = game.memo
                itemStats.text = context.getString(R.string.schedule_format_win_loss, item.winCount, item.lossCount)
                root.setOnClickListener { onItemClick(item) }
                listHeader.btnMore.setOnClickListener { onMoreClick(item) }
            }
        }

        companion object {
            private val DiffCallback = object : DiffUtil.ItemCallback<GameDisplayItem>() {
                override fun areItemsTheSame(oldItem: GameDisplayItem, newItem: GameDisplayItem): Boolean = oldItem.game.id == newItem.game.id
                override fun areContentsTheSame(oldItem: GameDisplayItem, newItem: GameDisplayItem): Boolean = oldItem == newItem
            }
        }
    }
}

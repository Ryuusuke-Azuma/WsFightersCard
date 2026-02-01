/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.ListitemScoreBinding
import com.mynet.kazekima.wsfighterscard.databinding.PageScheduleScoresBinding
import com.mynet.kazekima.wsfighterscard.db.Score
import com.mynet.kazekima.wsfighterscard.schedule.record.DeleteScoreDialogFragment
import com.mynet.kazekima.wsfighterscard.schedule.record.RecordScoreDialogFragment

class ScoresPageFragment : Fragment() {
    private val scheduleViewModel: ScheduleViewModel by activityViewModels()
    private val gamesViewModel: GamesViewModel by activityViewModels()
    private val scoresViewModel: ScoresViewModel by activityViewModels()

    private var _binding: PageScheduleScoresBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = PageScheduleScoresBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ScoreListAdapter { v, score -> showItemMenu(v, score) }
        _binding!!.recyclerViewScores.adapter = adapter
        scoresViewModel.scores.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            _binding!!.recyclerViewScores.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
        }

        gamesViewModel.selectedGame.observe(viewLifecycleOwner) { game ->
            if (game != null) {
                scoresViewModel.loadScores(game.game.id)
            } else {
                adapter.submitList(emptyList())
            }
        }

        childFragmentManager.setFragmentResultListener(RecordScoreDialogFragment.REQUEST_KEY, viewLifecycleOwner) { _, b ->
            if (b.getBoolean(RecordScoreDialogFragment.RESULT_SAVED)) {
                gamesViewModel.selectedGame.value?.let { scoresViewModel.loadScores(it.game.id) }
                scheduleViewModel.loadData()
            }
        }
        childFragmentManager.setFragmentResultListener(DeleteScoreDialogFragment.REQUEST_KEY, viewLifecycleOwner) { _, b ->
            if (b.getBoolean(DeleteScoreDialogFragment.RESULT_DELETED)) {
                gamesViewModel.selectedGame.value?.let { scoresViewModel.loadScores(it.game.id) }
                scheduleViewModel.loadData()
            }
        }
    }

    fun showAddDialog() {
        gamesViewModel.selectedGame.value?.let { item ->
            RecordScoreDialogFragment.newInstance(item.game.id, item.game.game_name, item.game.game_style.id)
                .show(childFragmentManager, RecordScoreDialogFragment.REQUEST_KEY)
        } ?: run {
            Toast.makeText(requireContext(), "Please select a game first", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showItemMenu(anchor: View, score: Score) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menu.add("Edit")
        popup.menu.add("Delete")
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.title) {
                "Edit" -> {
                    val game = gamesViewModel.selectedGame.value?.game
                    if (game != null) {
                        RecordScoreDialogFragment.newInstanceForEdit(
                            score.id,
                            score.game_id,
                            score.battle_deck,
                            score.matching_deck,
                            score.win_lose.id,
                            score.team_win_lose?.id ?: -1L,
                            score.memo,
                            game.game_style.id,
                            game.game_name
                        )
                            .show(childFragmentManager, RecordScoreDialogFragment.REQUEST_KEY)
                    }
                }
                "Delete" -> {
                    DeleteScoreDialogFragment.newInstance(score.id)
                        .show(childFragmentManager, DeleteScoreDialogFragment.REQUEST_KEY)
                }
            }
            true
        }
        popup.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class ScoreListAdapter(private val onMoreClick: (View, Score) -> Unit) :
        ListAdapter<Score, ScoreListAdapter.ViewHolder>(DiffCallback) {
        class ViewHolder(val binding: ListitemScoreBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(ListitemScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)
            with(holder.binding) {
                listHeader.headerText.text =
                    root.context.getString(R.string.schedule_format_match_index, position + 1)
                textDecks.text = root.context.getString(
                    R.string.schedule_format_match_decks,
                    item.battle_deck,
                    item.matching_deck
                )
                if (item.team_win_lose != null) {
                    textTeamResult.visibility = View.VISIBLE
                    textTeamResult.text = root.context.getString(
                        R.string.schedule_format_team_result_label,
                        item.team_win_lose!!.label
                    )
                    textPersonalResult.text = root.context.getString(
                        R.string.schedule_format_personal_result_label,
                        item.win_lose.label
                    )
                } else {
                    textTeamResult.visibility = View.GONE
                    textPersonalResult.text = item.win_lose.label
                }
                textMemo.text = item.memo
                listHeader.btnMore.setOnClickListener { onMoreClick(it, item) }
            }
        }

        companion object {
            private val DiffCallback = object : DiffUtil.ItemCallback<Score>() {
                override fun areItemsTheSame(old: Score, new: Score) = old.id == new.id
                override fun areContentsTheSame(old: Score, new: Score) = old == new
            }
        }
    }
}

/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private val gamesViewModel: GamesViewModel by activityViewModels()
    private val scoresViewModel: ScoresViewModel by activityViewModels()

    private var _binding: PageScheduleScoresBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = PageScheduleScoresBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ScoreListAdapter(
            onItemLongClick = { score -> showScheduleBottomSheet(score) },
            onMoreClick = { /* do nothing */ }
        )
        binding.recyclerScheduleScores.adapter = adapter
        scoresViewModel.scores.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.recyclerScheduleScores.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
        }

        gamesViewModel.selectedGame.observe(viewLifecycleOwner) { game ->
            if (game != null) {
                scoresViewModel.loadInitialScoresForGame(game.game.id)
            } else {
                adapter.submitList(emptyList())
            }
        }

        childFragmentManager.setFragmentResultListener(ScheduleBottomSheet.REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            val result = bundle.getString(ScheduleBottomSheet.RESULT_KEY)
            val itemId = bundle.getLong(ScheduleBottomSheet.ITEM_ID)
            val score = scoresViewModel.scores.value?.find { it.id == itemId } ?: return@setFragmentResultListener

            when (result) {
                ScheduleBottomSheet.ACTION_EDIT -> {
                    val game = gamesViewModel.selectedGame.value?.game
                    if (game != null) {
                        RecordScoreDialogFragment.newInstanceForEdit(
                            score.id,
                            score.game_id,
                            score.battle_deck,
                            score.matching_deck,
                            score.first_second.id,
                            score.win_lose.id,
                            score.team_win_lose?.id ?: -1L,
                            score.memo,
                            game.game_style.id,
                            game.game_name
                        )
                            .show(childFragmentManager, RecordScoreDialogFragment.REQUEST_KEY)
                    }
                }
                ScheduleBottomSheet.ACTION_DELETE -> {
                    DeleteScoreDialogFragment.newInstance(score.id)
                        .show(childFragmentManager, DeleteScoreDialogFragment.REQUEST_KEY)
                }
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

    private fun showScheduleBottomSheet(score: Score) {
        ScheduleBottomSheet.newInstance(score.id)
            .show(childFragmentManager, ScheduleBottomSheet.REQUEST_KEY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class ScoreListAdapter(
        private val onItemLongClick: (Score) -> Unit,
        private val onMoreClick: (Score) -> Unit
    ) :
        ListAdapter<Score, ScoreListAdapter.ViewHolder>(DiffCallback) {
        class ViewHolder(val binding: ListitemScoreBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(ListitemScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)
            with(holder.binding) {
                root.setOnLongClickListener {
                    onItemLongClick(item)
                    true
                }
                includeListitemHeader.textListitemHeader.text =
                    root.context.getString(R.string.schedule_format_match_number, position + 1)
                textScoreDecks.text = root.context.getString(
                    R.string.schedule_format_match_decks,
                    item.battle_deck,
                    item.matching_deck
                )
                textScoreFirstSecond.text = item.first_second.label
                if (item.team_win_lose != null) {
                    textScoreTeamResult.visibility = View.VISIBLE
                    textScoreTeamResult.text = root.context.getString(
                        R.string.schedule_format_team_result_label,
                        item.team_win_lose!!.label
                    )
                    textScorePersonalResult.text = root.context.getString(
                        R.string.schedule_format_personal_result_label,
                        item.win_lose.label
                    )
                } else {
                    textScoreTeamResult.visibility = View.GONE
                    textScorePersonalResult.text = item.win_lose.label
                }
                textScoreMemo.text = item.memo
                includeListitemHeader.buttonListitemMore.setOnClickListener { onMoreClick(item) }
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

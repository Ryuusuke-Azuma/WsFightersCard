/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule.record

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.DialogRecordScoreBinding
import com.mynet.kazekima.wsfighterscard.databinding.ListitemScoreBinding
import com.mynet.kazekima.wsfighterscard.db.Score
import com.mynet.kazekima.wsfighterscard.db.enums.GameStyle
import com.mynet.kazekima.wsfighterscard.db.enums.TeamResult
import com.mynet.kazekima.wsfighterscard.db.enums.TeamWinLose
import com.mynet.kazekima.wsfighterscard.db.enums.WinLose

class RecordScoreDialogFragment : DialogFragment() {

    private val viewModel: RecordViewModel by viewModels()
    private var _binding: DialogRecordScoreBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var scoreAdapter: ScoreListAdapter
    private var hasChanges = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogRecordScoreBinding.inflate(layoutInflater)

        val gameId = arguments?.getLong(ARG_GAME_ID) ?: -1L
        val gameName = arguments?.getString(ARG_GAME_NAME) ?: ""
        val styleId = arguments?.getLong(ARG_GAME_STYLE) ?: 0L
        val style = GameStyle.fromId(styleId)

        binding.textGameTitle.text = gameName

        if (style == GameStyle.TEAMS) {
            binding.layoutTeamOptions.visibility = View.VISIBLE
        } else {
            binding.layoutTeamOptions.visibility = View.GONE
        }

        scoreAdapter = ScoreListAdapter()
        binding.recyclerScores.adapter = scoreAdapter

        loadScores(gameId)

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_record_score)
            .setView(binding.root)
            .setPositiveButton(R.string.dialog_record_ok) { _, _ ->
                saveNewScore(gameId, style)
            }
            .setNegativeButton(R.string.dialog_record_cancel) { _, _ ->
                if (hasChanges) notifyUpdate()
            }
            .create()
    }

    private fun loadScores(gameId: Long) {
        viewModel.getScoresForGame(gameId) { scores ->
            scoreAdapter.submitList(scores)
            if (scores.isNotEmpty()) {
                binding.recyclerScores.visibility = View.VISIBLE
                binding.recyclerScores.scrollToPosition(scores.size - 1)
            } else {
                binding.recyclerScores.visibility = View.GONE
            }
        }
    }

    private fun saveNewScore(gameId: Long, style: GameStyle) {
        val myDeck = binding.editBattleDeck.text.toString()
        val opponentDeck = binding.editMatchingDeck.text.toString()
        val winLose = if (binding.radioWin.isChecked) WinLose.WIN else WinLose.LOSE
        val memo = binding.editScoreMemo.text.toString()

        var teamResult: TeamResult? = null
        var teamWinLose: TeamWinLose? = null

        if (style == GameStyle.TEAMS) {
            teamResult = when (binding.radioGroupTeamResult.checkedRadioButtonId) {
                R.id.radio_team_3_0 -> TeamResult.WIN_3_0
                R.id.radio_team_2_1 -> TeamResult.WIN_2_1
                R.id.radio_team_1_2 -> TeamResult.LOSE_1_2
                R.id.radio_team_0_3 -> TeamResult.LOSE_0_3
                else -> TeamResult.WIN_2_1
            }
            teamWinLose = if (binding.radioTeamWin.isChecked) TeamWinLose.WIN else TeamWinLose.LOSE
        }

        if (gameId != -1L) {
            viewModel.addScore(
                gameId = gameId,
                battleDeck = myDeck,
                matchingDeck = opponentDeck,
                winLose = winLose,
                teamResult = teamResult,
                teamWinLose = teamWinLose,
                memo = memo
            ) {
                hasChanges = true
                loadScores(gameId)
                clearInputFields()
            }
        }
    }

    private fun clearInputFields() {
        binding.editMatchingDeck.text?.clear()
        binding.editScoreMemo.text?.clear()
    }

    private fun notifyUpdate() {
        setFragmentResult(REQUEST_KEY, Bundle().apply {
            putBoolean(RESULT_SAVED, true)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (hasChanges) notifyUpdate()
        _binding = null
    }

    private class ScoreListAdapter : ListAdapter<Score, ScoreListAdapter.ViewHolder>(DiffCallback) {
        class ViewHolder(val binding: ListitemScoreBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ListitemScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)
            with(holder.binding) {
                textMatchIndex.text = "${position + 1}."
                textDecks.text = "${item.battle_deck} vs ${item.matching_deck}"
                textResult.text = item.win_lose.label
                
                if (item.team_result != null) {
                    textResult.text = "${item.win_lose.label} (${item.team_result!!.label} ${item.team_win_lose?.label ?: ""})"
                }

                textMemo.text = item.memo
                textMemo.visibility = if (item.memo.isNotBlank()) View.VISIBLE else View.GONE
            }
        }

        companion object {
            private val DiffCallback = object : DiffUtil.ItemCallback<Score>() {
                override fun areItemsTheSame(old: Score, new: Score) = old.id == new.id
                override fun areContentsTheSame(old: Score, new: Score) = old == new
            }
        }
    }

    companion object {
        const val REQUEST_KEY = "record_score_request"
        const val RESULT_SAVED = "result_saved"
        private const val ARG_GAME_ID = "game_id"
        private const val ARG_GAME_NAME = "game_name"
        private const val ARG_GAME_STYLE = "game_style"

        fun newInstance(gameId: Long, gameName: String, styleId: Long): RecordScoreDialogFragment {
            return RecordScoreDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_GAME_ID, gameId)
                    putString(ARG_GAME_NAME, gameName)
                    putLong(ARG_GAME_STYLE, styleId)
                }
            }
        }
    }
}

/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule.record

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.DialogRecordScoreBinding
import com.mynet.kazekima.wsfighterscard.db.enums.FirstSecond
import com.mynet.kazekima.wsfighterscard.db.enums.GameStyle
import com.mynet.kazekima.wsfighterscard.db.enums.TeamWinLose
import com.mynet.kazekima.wsfighterscard.db.enums.WinLose
import com.mynet.kazekima.wsfighterscard.schedule.DeckPickerViewModel
import com.mynet.kazekima.wsfighterscard.schedule.ScoresViewModel
import com.mynet.kazekima.wsfighterscard.schedule.models.FighterItem
import com.mynet.kazekima.wsfighterscard.schedule.widget.DeckPickerFragment
import kotlinx.coroutines.launch

class RecordScoreDialogFragment : DialogFragment() {

    private val scoresViewModel: ScoresViewModel by activityViewModels()
    private val deckPickerViewModel: DeckPickerViewModel by viewModels()
    private var _binding: DialogRecordScoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogRecordScoreBinding.inflate(layoutInflater)

        val scoreId = arguments?.getLong(ARG_SCORE_ID, -1L) ?: -1L
        val gameId = arguments?.getLong(ARG_GAME_ID) ?: -1L
        val gameName = arguments?.getString(ARG_GAME_NAME) ?: ""
        val styleId = arguments?.getLong(ARG_GAME_STYLE) ?: 0L
        val style = GameStyle.fromId(styleId)

        val initialMyDeck = arguments?.getString(ARG_MY_DECK) ?: ""
        val initialOpponentDeck = arguments?.getString(ARG_OPPONENT_DECK) ?: ""
        val initialFirstSecondId = arguments?.getLong(ARG_FIRST_SECOND, 0L) ?: 0L
        val initialWinLoseId = arguments?.getLong(ARG_WIN_LOSE, 1L) ?: 1L
        val initialTeamWinLoseId = arguments?.getLong(ARG_TEAM_WIN_LOSE, -1L) ?: -1L
        val initialMemo = arguments?.getString(ARG_MEMO) ?: ""

        binding.textScoreGameTitle.text = gameName
        binding.editScoreBattleDeck.setText(initialMyDeck)
        binding.editScoreMatchingDeck.setText(initialOpponentDeck)
        binding.editScoreMemo.setText(initialMemo)

        binding.layoutBattleDeck.setEndIconOnClickListener {
            lifecycleScope.launch {
                val fighters = deckPickerViewModel.getFighters(true).map { FighterItem(it.id, it.name) }
                DeckPickerFragment.newInstance(true, fighters).show(childFragmentManager, DeckPickerFragment.REQUEST_KEY)
            }
        }

        binding.layoutMatchingDeck.setEndIconOnClickListener {
            lifecycleScope.launch {
                val fighters = deckPickerViewModel.getFighters(false).map { FighterItem(it.id, it.name) }
                DeckPickerFragment.newInstance(false, fighters).show(childFragmentManager, DeckPickerFragment.REQUEST_KEY)
            }
        }

        childFragmentManager.setFragmentResultListener(DeckPickerFragment.REQUEST_KEY, this) { _, bundle ->
            val deckName = bundle.getString(DeckPickerFragment.RESULT_DECK_NAME)
            val isMyDeck = bundle.getBoolean(DeckPickerFragment.RESULT_IS_MY_DECK)
            if (isMyDeck) {
                binding.editScoreBattleDeck.setText(deckName)
            } else {
                binding.editScoreMatchingDeck.setText(deckName)
            }
        }

        val checkedFirstSecondId = if (initialFirstSecondId == FirstSecond.FIRST.id) R.id.radio_score_first else R.id.radio_score_second
        binding.radioGroupScoreFirstSecond.check(checkedFirstSecondId)

        val checkedWinLoseId = if (initialWinLoseId == WinLose.WIN.id) R.id.radio_score_win else R.id.radio_score_lose
        binding.radioGroupScorePersonalResult.check(checkedWinLoseId)

        if (style == GameStyle.TEAMS) {
            binding.layoutScoreTeamOptions.visibility = View.VISIBLE
            val teamWinLose = TeamWinLose.fromId(initialTeamWinLoseId)
            when (teamWinLose) {
                TeamWinLose.WIN_3_0 -> binding.radioGroupScoreTeamResult.check(R.id.radio_score_team_3_0)
                TeamWinLose.WIN_2_1 -> binding.radioGroupScoreTeamResult.check(R.id.radio_score_team_2_1)
                TeamWinLose.LOSE_1_2 -> binding.radioGroupScoreTeamResult.check(R.id.radio_score_team_1_2)
                TeamWinLose.LOSE_0_3 -> binding.radioGroupScoreTeamResult.check(R.id.radio_score_team_0_3)
                else -> binding.radioGroupScoreTeamResult.check(R.id.radio_score_team_2_1) // デフォルト
            }
        } else {
            binding.layoutScoreTeamOptions.visibility = View.GONE
        }

        val isEdit = scoreId != -1L
        val title = if (isEdit) R.string.dialog_edit_score else R.string.dialog_record_score

        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(binding.root)
            .setPositiveButton(if (isEdit) R.string.dialog_edit_ok else R.string.dialog_record_ok) { _, _ ->
                val myDeck = binding.editScoreBattleDeck.text.toString()
                val opponentDeck = binding.editScoreMatchingDeck.text.toString()
                val memo = binding.editScoreMemo.text.toString()

                val firstSecond = when (binding.radioGroupScoreFirstSecond.checkedRadioButtonId) {
                    R.id.radio_score_first -> FirstSecond.FIRST
                    else -> FirstSecond.SECOND
                }

                val winLose = when (binding.radioGroupScorePersonalResult.checkedRadioButtonId) {
                    R.id.radio_score_win -> WinLose.WIN
                    else -> WinLose.LOSE
                }

                var teamWinLose: TeamWinLose? = null

                if (style == GameStyle.TEAMS) {
                    teamWinLose = when (binding.radioGroupScoreTeamResult.checkedRadioButtonId) {
                        R.id.radio_score_team_3_0 -> TeamWinLose.WIN_3_0
                        R.id.radio_score_team_2_1 -> TeamWinLose.WIN_2_1
                        R.id.radio_score_team_1_2 -> TeamWinLose.LOSE_1_2
                        R.id.radio_score_team_0_3 -> TeamWinLose.LOSE_0_3
                        else -> TeamWinLose.WIN_2_1
                    }
                }

                if (scoreId == -1L) {
                    if (gameId != -1L) {
                        scoresViewModel.addScore(gameId, myDeck, opponentDeck, firstSecond, winLose, teamWinLose, memo)
                    }
                } else {
                    scoresViewModel.updateScore(scoreId, myDeck, opponentDeck, firstSecond, winLose, teamWinLose, memo)
                }
                parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(RESULT_SAVED to true))
            }
            .setNegativeButton(if (isEdit) R.string.dialog_edit_cancel else R.string.dialog_record_cancel, null)
            .create()
    }

    companion object {
        const val REQUEST_KEY = "RecordScoreDialogFragmentRequest"
        const val RESULT_SAVED = "result_saved"
        private const val ARG_SCORE_ID = "score_id"
        private const val ARG_GAME_ID = "game_id"
        private const val ARG_GAME_NAME = "game_name"
        private const val ARG_GAME_STYLE = "game_style"
        private const val ARG_MY_DECK = "my_deck"
        private const val ARG_OPPONENT_DECK = "opponent_deck"
        private const val ARG_FIRST_SECOND = "first_second"
        private const val ARG_WIN_LOSE = "win_lose"
        private const val ARG_TEAM_WIN_LOSE = "team_win_lose"
        private const val ARG_MEMO = "memo"

        fun newInstance(gameId: Long, gameName: String, styleId: Long): RecordScoreDialogFragment {
            return RecordScoreDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_GAME_ID, gameId)
                    putString(ARG_GAME_NAME, gameName)
                    putLong(ARG_GAME_STYLE, styleId)
                }
            }
        }

        fun newInstanceForEdit(id: Long, gameId: Long, myDeck: String, opponentDeck: String, firstSecondId: Long, winLoseId: Long, teamWinLoseId: Long, memo: String, styleId: Long, gameName: String): RecordScoreDialogFragment {
            return RecordScoreDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_SCORE_ID, id)
                    putLong(ARG_GAME_ID, gameId)
                    putString(ARG_MY_DECK, myDeck)
                    putString(ARG_OPPONENT_DECK, opponentDeck)
                    putLong(ARG_FIRST_SECOND, firstSecondId)
                    putLong(ARG_WIN_LOSE, winLoseId)
                    putLong(ARG_TEAM_WIN_LOSE, teamWinLoseId)
                    putString(ARG_MEMO, memo)
                    putLong(ARG_GAME_STYLE, styleId)
                    putString(ARG_GAME_NAME, gameName)
                }
            }
        }
    }
}

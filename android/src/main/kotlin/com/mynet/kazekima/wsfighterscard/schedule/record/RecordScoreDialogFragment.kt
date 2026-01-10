/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule.record

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.DialogRecordScoreBinding
import com.mynet.kazekima.wsfighterscard.db.enums.GameStyle
import com.mynet.kazekima.wsfighterscard.db.enums.TeamWinLose
import com.mynet.kazekima.wsfighterscard.db.enums.WinLose

class RecordScoreDialogFragment : DialogFragment() {

    private val viewModel: RecordViewModel by viewModels()
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
        val initialWinLoseId = arguments?.getLong(ARG_WIN_LOSE, 1L) ?: 1L
        val initialTeamWinLoseId = arguments?.getLong(ARG_TEAM_WIN_LOSE, -1L) ?: -1L
        val initialMemo = arguments?.getString(ARG_MEMO) ?: ""

        binding.textGameTitle.text = gameName
        binding.editBattleDeck.setText(initialMyDeck)
        binding.editMatchingDeck.setText(initialOpponentDeck)
        binding.editScoreMemo.setText(initialMemo)

        // 編集・新規に関わらず形式に応じた表示切り替え
        if (style == GameStyle.TEAMS) {
            binding.layoutTeamOptions.visibility = View.VISIBLE
            binding.layoutPersonalOptions.visibility = View.GONE
            
            // チーム戦績の復元
            val teamWinLose = TeamWinLose.fromId(initialTeamWinLoseId)
            when (teamWinLose) {
                TeamWinLose.WIN_3_0 -> binding.radioGroupTeamResult.check(R.id.radio_team_3_0)
                TeamWinLose.WIN_2_1 -> binding.radioGroupTeamResult.check(R.id.radio_team_2_1)
                TeamWinLose.LOSE_1_2 -> binding.radioGroupTeamResult.check(R.id.radio_team_1_2)
                TeamWinLose.LOSE_0_3 -> binding.radioGroupTeamResult.check(R.id.radio_team_0_3)
                else -> binding.radioGroupTeamResult.check(R.id.radio_team_2_1) // デフォルト
            }
        } else {
            binding.layoutTeamOptions.visibility = View.GONE
            binding.layoutPersonalOptions.visibility = View.VISIBLE
            
            // 個人結果の復元
            if (initialWinLoseId == WinLose.WIN.id) {
                binding.radioWin.isChecked = true
            } else {
                binding.radioLose.isChecked = true
            }
        }

        val isEdit = scoreId != -1L

        return AlertDialog.Builder(requireContext())
            .setTitle(if (isEdit) R.string.dialog_edit_score else R.string.dialog_record_score)
            .setView(binding.root)
            .setPositiveButton(if (isEdit) R.string.dialog_edit_ok else R.string.dialog_record_ok) { _, _ ->
                saveScore(scoreId, gameId, style)
            }
            .setNegativeButton(if (isEdit) R.string.dialog_edit_cancel else R.string.dialog_record_cancel, null)
            .create()
    }

    private fun saveScore(scoreId: Long, gameId: Long, style: GameStyle) {
        val myDeck = binding.editBattleDeck.text.toString()
        val opponentDeck = binding.editMatchingDeck.text.toString()
        val memo = binding.editScoreMemo.text.toString()

        val winLose: WinLose
        var teamWinLose: TeamWinLose? = null

        if (style == GameStyle.TEAMS) {
            teamWinLose = when (binding.radioGroupTeamResult.checkedRadioButtonId) {
                R.id.radio_team_3_0 -> TeamWinLose.WIN_3_0
                R.id.radio_team_2_1 -> TeamWinLose.WIN_2_1
                R.id.radio_team_1_2 -> TeamWinLose.LOSE_1_2
                R.id.radio_team_0_3 -> TeamWinLose.LOSE_0_3
                else -> TeamWinLose.WIN_2_1
            }
            winLose = teamWinLose.winLose
        } else {
            winLose = if (binding.radioWin.isChecked) WinLose.WIN else WinLose.LOSE
        }

        if (scoreId == -1L) {
            if (gameId != -1L) {
                viewModel.addScore(gameId, myDeck, opponentDeck, winLose, teamWinLose, memo) { notifySaved() }
            }
        } else {
            viewModel.updateScore(scoreId, myDeck, opponentDeck, winLose, teamWinLose, memo) { notifySaved() }
        }
    }

    private fun notifySaved() {
        setFragmentResult(REQUEST_KEY, Bundle().apply { putBoolean(RESULT_SAVED, true) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY = "record_score_request"
        const val RESULT_SAVED = "result_saved"
        private const val ARG_SCORE_ID = "score_id"
        private const val ARG_GAME_ID = "game_id"
        private const val ARG_GAME_NAME = "game_name"
        private const val ARG_GAME_STYLE = "game_style"
        private const val ARG_MY_DECK = "my_deck"
        private const val ARG_OPPONENT_DECK = "opponent_deck"
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

        fun newInstanceForEdit(id: Long, gameId: Long, myDeck: String, opponentDeck: String, winLoseId: Long, teamWinLoseId: Long, memo: String, styleId: Long, gameName: String): RecordScoreDialogFragment {
            return RecordScoreDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_SCORE_ID, id)
                    putLong(ARG_GAME_ID, gameId)
                    putString(ARG_MY_DECK, myDeck)
                    putString(ARG_OPPONENT_DECK, opponentDeck)
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

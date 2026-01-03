/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule.record

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.DialogRecordScoreBinding

/**
 * 対戦結果（スコア）を登録するためのダイアログ
 */
class RecordScoreDialogFragment : DialogFragment() {

    private val viewModel: RecordViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val binding = DialogRecordScoreBinding.inflate(layoutInflater)
        
        val gameId = arguments?.getLong(ARG_GAME_ID) ?: -1L
        val gameName = arguments?.getString(ARG_GAME_NAME) ?: ""

        binding.textGameTitle.text = gameName

        return AlertDialog.Builder(context)
            .setTitle(R.string.dialog_record_score)
            .setView(binding.root)
            .setPositiveButton(R.string.dialog_record_ok) { _, _ ->
                val matchingDeck = binding.editMatchingDeck.text.toString()
                val winOrLose = if (binding.radioWin.isChecked) 1L else 0L
                val memo = binding.editScoreMemo.text.toString()

                if (gameId != -1L) {
                    // ViewModel 経由でデータベースへ保存
                    viewModel.addScore(gameId, matchingDeck, winOrLose, memo) {
                        // 保存完了を FragmentResultAPI で通知
                        setFragmentResult(REQUEST_KEY, Bundle().apply {
                            putBoolean(RESULT_SAVED, true)
                        })
                    }
                }
            }
            .setNegativeButton(R.string.dialog_record_cancel, null)
            .create()
    }

    companion object {
        const val REQUEST_KEY = "score_request"
        const val RESULT_SAVED = "result_saved"
        private const val ARG_GAME_ID = "game_id"
        private const val ARG_GAME_NAME = "game_name"

        fun newInstance(gameId: Long, gameName: String): RecordScoreDialogFragment {
            val fragment = RecordScoreDialogFragment()
            fragment.arguments = Bundle().apply {
                putLong(ARG_GAME_ID, gameId)
                putString(ARG_GAME_NAME, gameName)
            }
            return fragment
        }
    }
}

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

class DeleteScoreDialogFragment : DialogFragment() {

    private val viewModel: RecordViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val scoreId = arguments?.getLong(ARG_SCORE_ID) ?: -1L

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_delete_confirm_title)
            .setMessage(R.string.dialog_delete_score_confirm_message)
            .setPositiveButton(R.string.dialog_delete_ok) { _, _ ->
                if (scoreId != -1L) {
                    viewModel.deleteScore(scoreId) {
                        setFragmentResult(REQUEST_KEY, Bundle().apply { putBoolean(RESULT_DELETED, true) })
                    }
                }
            }
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .create()
    }

    companion object {
        const val REQUEST_KEY = "delete_score_request"
        const val RESULT_DELETED = "result_deleted"
        private const val ARG_SCORE_ID = "score_id"

        fun newInstance(scoreId: Long): DeleteScoreDialogFragment {
            return DeleteScoreDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_SCORE_ID, scoreId)
                }
            }
        }
    }
}

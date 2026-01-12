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

class DeleteGameDialogFragment : DialogFragment() {

    private val viewModel: RecordViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val gameId = arguments?.getLong(ARG_GAME_ID) ?: -1L
        val gameName = arguments?.getString(ARG_GAME_NAME) ?: ""

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_delete_confirm_title)
            .setMessage(getString(R.string.dialog_delete_confirm_message, gameName))
            .setPositiveButton(R.string.dialog_delete_ok) { _, _ ->
                if (gameId != -1L) {
                    viewModel.deleteGame(gameId) {
                        setFragmentResult(REQUEST_KEY, Bundle().apply {
                            putBoolean(RESULT_DELETED, true)
                        })
                    }
                }
            }
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .create()
    }

    companion object {
        const val REQUEST_KEY = "delete_request"
        const val RESULT_DELETED = "result_deleted"
        private const val ARG_GAME_ID = "game_id"
        private const val ARG_GAME_NAME = "game_name"

        fun newInstance(gameId: Long, gameName: String): DeleteGameDialogFragment {
            return DeleteGameDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_GAME_ID, gameId)
                    putString(ARG_GAME_NAME, gameName)
                }
            }
        }
    }
}

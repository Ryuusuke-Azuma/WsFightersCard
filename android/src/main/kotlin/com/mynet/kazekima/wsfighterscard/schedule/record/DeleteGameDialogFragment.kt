/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule.record

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.schedule.GamesViewModel

class DeleteGameDialogFragment : DialogFragment() {

    private val viewModel: GamesViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val gameId = arguments?.getLong(ARG_GAME_ID) ?: -1L
        val gameName = arguments?.getString(ARG_GAME_NAME) ?: ""

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_delete_confirm_title)
            .setMessage(getString(R.string.dialog_delete_confirm_message, gameName))
            .setPositiveButton(R.string.dialog_delete_ok) { _, _ ->
                if (gameId != -1L) {
                    viewModel.deleteGame(gameId)
                    parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(RESULT_DELETED to true))
                }
            }
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .create()
    }

    companion object {
        const val REQUEST_KEY = "DeleteGameDialogFragment"
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

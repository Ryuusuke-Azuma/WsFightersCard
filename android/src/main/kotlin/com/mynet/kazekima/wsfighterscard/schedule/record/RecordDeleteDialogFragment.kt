/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule.record

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.mynet.kazekima.wsfighterscard.R

class RecordDeleteDialogFragment : DialogFragment() {

    private val viewModel: RecordViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val gameId = arguments?.getLong(ARG_GAME_ID) ?: -1L
        val gameName = arguments?.getString(ARG_GAME_NAME) ?: ""
        val position = arguments?.getInt(ARG_POSITION) ?: -1

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_delete_confirm_title)
            .setMessage(getString(R.string.dialog_delete_confirm_message, gameName))
            .setPositiveButton(R.string.dialog_delete_ok) { _: DialogInterface, _: Int ->
                if (gameId != -1L) {
                    viewModel.deleteGame(gameId) {
                        sendResult(true, position)
                    }
                }
            }
            .setNegativeButton(R.string.dialog_delete_cancel) { _: DialogInterface, _: Int ->
                sendResult(false, position)
            }
            .create()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        val position = arguments?.getInt(ARG_POSITION) ?: -1
        sendResult(false, position)
    }

    private fun sendResult(isDeleted: Boolean, position: Int) {
        setFragmentResult(REQUEST_KEY, Bundle().apply {
            putBoolean(RESULT_DELETED, isDeleted)
            putInt(RESULT_POSITION, position)
        })
    }

    companion object {
        const val REQUEST_KEY = "delete_request"
        const val RESULT_DELETED = "result_deleted"
        const val RESULT_POSITION = "result_position"
        private const val ARG_GAME_ID = "game_id"
        private const val ARG_GAME_NAME = "game_name"
        private const val ARG_POSITION = "arg_position"

        fun newInstance(gameId: Long, gameName: String, position: Int): RecordDeleteDialogFragment {
            return RecordDeleteDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_GAME_ID, gameId)
                    putString(ARG_GAME_NAME, gameName)
                    putInt(ARG_POSITION, position)
                }
            }
        }
    }
}

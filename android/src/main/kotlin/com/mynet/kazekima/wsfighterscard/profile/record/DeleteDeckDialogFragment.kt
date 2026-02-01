/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.profile.record

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.profile.DecksViewModel

class DeleteDeckDialogFragment : DialogFragment() {

    private val viewModel: DecksViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val deckId = arguments?.getLong(ARG_ID)!!
        val deckName = arguments?.getString(ARG_NAME) ?: ""

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_delete_confirm_title)
            .setMessage(getString(R.string.dialog_delete_deck_confirm_message, deckName))
            .setPositiveButton(R.string.dialog_delete_ok) { _, _ ->
                viewModel.deleteDeck(deckId)
                parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(RESULT_DELETED to true))
            }
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .create()
    }

    companion object {
        const val REQUEST_KEY = "DeleteDeckDialogFragmentRequest"
        const val RESULT_DELETED = "result_deleted"

        private const val ARG_ID = "id"
        private const val ARG_NAME = "name"

        fun newInstance(id: Long, name: String): DeleteDeckDialogFragment {
            return DeleteDeckDialogFragment().apply {
                arguments = bundleOf(
                    ARG_ID to id,
                    ARG_NAME to name
                )
            }
        }
    }
}

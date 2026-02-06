/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.profile.record

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.profile.DecksViewModel

class RecordDeckDialogFragment : DialogFragment() {

    private val viewModel: DecksViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val isEdit = arguments?.containsKey(ARG_ID) ?: false
        val title = if (isEdit) R.string.dialog_edit_deck else R.string.dialog_record_deck
        val positiveButtonText = if (isEdit) R.string.dialog_edit_ok else R.string.dialog_record_ok

        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_record_deck, null)
        val nameEditText = view.findViewById<EditText>(R.id.edit_text_deck_name)
        val memoEditText = view.findViewById<EditText>(R.id.edit_text_memo)

        if (isEdit) {
            nameEditText.setText(arguments?.getString(ARG_NAME))
            memoEditText.setText(arguments?.getString(ARG_MEMO))
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(view)
            .setPositiveButton(positiveButtonText) { _, _ ->
                val fighterId = arguments?.getLong(ARG_FIGHTER_ID)!!
                if (isEdit) {
                    val id = arguments?.getLong(ARG_ID)!!
                    viewModel.updateDeck(id, nameEditText.text.toString(), memoEditText.text.toString())
                } else {
                    viewModel.addDeck(fighterId, nameEditText.text.toString(), memoEditText.text.toString())
                }
                parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(RESULT_SAVED to true))
            }
            .setNegativeButton(R.string.dialog_record_cancel, null)
            .create()
    }

    companion object {
        const val REQUEST_KEY = "RecordDeckDialogFragmentRequest"
        const val RESULT_SAVED = "result_saved"

        private const val ARG_ID = "id"
        private const val ARG_FIGHTER_ID = "fighter_id"
        private const val ARG_NAME = "name"
        private const val ARG_MEMO = "memo"

        fun newInstance(fighterId: Long): RecordDeckDialogFragment {
            return RecordDeckDialogFragment().apply {
                arguments = bundleOf(ARG_FIGHTER_ID to fighterId)
            }
        }

        fun newInstanceForEdit(id: Long, name: String, memo: String): RecordDeckDialogFragment {
            return RecordDeckDialogFragment().apply {
                arguments = bundleOf(
                    ARG_ID to id,
                    ARG_NAME to name,
                    ARG_MEMO to memo
                )
            }
        }
    }
}

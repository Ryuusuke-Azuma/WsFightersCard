/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.profile.record

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.DialogRecordDeckBinding
import com.mynet.kazekima.wsfighterscard.profile.DecksViewModel

class RecordDeckDialogFragment : DialogFragment() {

    private var _binding: DialogRecordDeckBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DecksViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogRecordDeckBinding.inflate(layoutInflater)
        val fighterId = requireArguments().getLong(ARG_FIGHTER_ID)

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_record_deck)
            .setView(binding.root)
            .setPositiveButton(R.string.dialog_record_ok) { _, _ ->
                val name = binding.editTextDeckName.text.toString()
                val memo = binding.editTextMemo.text.toString()
                if (name.isNotBlank()) {
                    viewModel.addDeck(fighterId, name, memo)
                    parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(RESULT_SAVED to true))
                }
            }
            .setNegativeButton(R.string.dialog_record_cancel, null)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY = "RecordDeckDialogFragment"
        const val RESULT_SAVED = "result_saved"
        private const val ARG_FIGHTER_ID = "fighter_id"

        fun newInstance(fighterId: Long) = RecordDeckDialogFragment().apply {
            arguments = bundleOf(ARG_FIGHTER_ID to fighterId)
        }
    }
}

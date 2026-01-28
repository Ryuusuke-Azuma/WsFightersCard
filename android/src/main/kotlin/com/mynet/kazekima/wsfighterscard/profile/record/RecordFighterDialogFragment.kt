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
import com.mynet.kazekima.wsfighterscard.databinding.DialogRecordFighterBinding
import com.mynet.kazekima.wsfighterscard.profile.FightersViewModel

class RecordFighterDialogFragment : DialogFragment() {

    private var _binding: DialogRecordFighterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FightersViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogRecordFighterBinding.inflate(layoutInflater)

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_record_fighter)
            .setView(binding.root)
            .setPositiveButton(R.string.dialog_record_ok) { _, _ ->
                val name = binding.editTextFighterName.text.toString()
                val memo = binding.editTextMemo.text.toString()
                if (name.isNotBlank()) {
                    viewModel.addFighter(name, memo)
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
        const val REQUEST_KEY = "RecordFighterDialogFragment"
        const val RESULT_SAVED = "result_saved"

        fun newInstance() = RecordFighterDialogFragment()
    }
}

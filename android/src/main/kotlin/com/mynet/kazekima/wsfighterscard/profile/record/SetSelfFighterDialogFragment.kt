/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.profile.record

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.mynet.kazekima.wsfighterscard.R

class SetSelfFighterDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val fighterId = requireArguments().getLong(ARG_FIGHTER_ID)
        val fighterName = requireArguments().getString(ARG_FIGHTER_NAME)

        val message = getString(R.string.dialog_set_self_confirm_message, fighterName)

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_set_self_confirm_title)
            .setMessage(message)
            .setPositiveButton(R.string.dialog_set_self_ok) { _, _ ->
                setFragmentResult(REQUEST_KEY, bundleOf(RESULT_OK to fighterId))
            }
            .setNegativeButton(R.string.dialog_set_self_cancel, null)
            .create()
    }

    companion object {
        const val REQUEST_KEY = "SetSelfFighterDialogRequest"
        const val RESULT_OK = "result_ok"

        private const val ARG_FIGHTER_ID = "fighter_id"
        private const val ARG_FIGHTER_NAME = "fighter_name"

        fun newInstance(fighterId: Long, fighterName: String): SetSelfFighterDialogFragment {
            return SetSelfFighterDialogFragment().apply {
                arguments = bundleOf(
                    ARG_FIGHTER_ID to fighterId,
                    ARG_FIGHTER_NAME to fighterName
                )
            }
        }
    }
}

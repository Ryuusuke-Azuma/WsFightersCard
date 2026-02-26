/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule.widget

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment

class TournamentPickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(ARG_TITLE) ?: ""
        val items = arguments?.getStringArray(ARG_ITEMS) ?: emptyArray()

        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setItems(items) { _, which ->
                parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(
                    RESULT_SELECTED_ITEM to items[which]
                ))
            }
            .create()
    }

    companion object {
        const val REQUEST_KEY = "TournamentPickerRequest"
        const val RESULT_SELECTED_ITEM = "result_selected_item"
        private const val ARG_TITLE = "arg_title"
        private const val ARG_ITEMS = "arg_items"

        fun newInstance(title: String, items: Array<String>): TournamentPickerFragment {
            return TournamentPickerFragment().apply {
                arguments = bundleOf(
                    ARG_TITLE to title,
                    ARG_ITEMS to items
                )
            }
        }
    }
}

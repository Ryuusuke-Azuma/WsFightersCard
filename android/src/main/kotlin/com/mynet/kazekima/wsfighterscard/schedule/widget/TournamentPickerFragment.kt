/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule.widget

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class TournamentPickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(ARG_TITLE) ?: ""
        val items = arguments?.getStringArray(ARG_ITEMS) ?: emptyArray()

        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setItems(items) { _, which ->
                parentFragmentManager.setFragmentResult(REQUEST_KEY, Bundle().apply {
                    putString(RESULT_SELECTED_ITEM, items[which])
                })
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
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putStringArray(ARG_ITEMS, items)
                }
            }
        }
    }
}

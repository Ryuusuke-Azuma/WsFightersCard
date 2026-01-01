/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.record

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.mynet.kazekima.wsfighterscard.R

class RecordDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = context ?: return super.onCreateDialog(savedInstanceState)

        val builder = AlertDialog.Builder(context)
        builder.setMessage(R.string.dialog_record_fighters)
                .setPositiveButton(R.string.dialog_record_fighters_ok) { dialog, id ->
                    // OK
                }
                .setNegativeButton(R.string.dialog_record_fighters_cancel) { dialog, id ->
                    // User cancelled the dialog
                }
        // Create the AlertDialog object and return it
        return builder.create()
    }
}
/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule.record

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.DialogRecordGameBinding
import com.mynet.kazekima.wsfighterscard.schedule.ScheduleViewModel
import java.time.format.DateTimeFormatter

class RecordGameDialogFragment : DialogFragment() {

    private val viewModel: RecordViewModel by viewModels()
    private val scheduleViewModel: ScheduleViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val binding = DialogRecordGameBinding.inflate(layoutInflater)

        val selectedDate = scheduleViewModel.selectedDate.value
        val dateString = selectedDate?.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        binding.editGameDate.setText(dateString)

        return AlertDialog.Builder(context)
            .setTitle(R.string.dialog_record_game)
            .setView(binding.root)
            .setPositiveButton(R.string.dialog_record_ok) { _: DialogInterface, _: Int ->
                val name = binding.editGameName.text.toString()
                val date = binding.editGameDate.text.toString()
                val memo = binding.editMemo.text.toString()

                if (name.isNotBlank()) {
                    viewModel.addGame(name, date, memo) {
                        setFragmentResult(REQUEST_KEY, Bundle().apply {
                            putBoolean(RESULT_SAVED, true)
                        })
                    }
                }
            }
            .setNegativeButton(R.string.dialog_record_cancel, null)
            .create()
    }

    companion object {
        const val REQUEST_KEY = "game_request"
        const val RESULT_SAVED = "result_saved"
    }
}

/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.mynet.kazekima.wsfighterscard.MainViewModel
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.DialogRecordScheduleBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RecordDialogFragment : DialogFragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        // 警告修正: layoutInflater を直接使用
        val binding = DialogRecordScheduleBinding.inflate(layoutInflater)

        // java.time を使用したモダンな日付取得
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        binding.editGameDate.setText(today)

        return AlertDialog.Builder(context)
            .setTitle(R.string.dialog_record_schedule)
            .setView(binding.root)
            .setPositiveButton(R.string.dialog_record_ok) { _, _ ->
                val name = binding.editGameName.text.toString()
                val date = binding.editGameDate.text.toString()
                val deck = binding.editBattleDeck.text.toString()
                val memo = binding.editMemo.text.toString()

                if (name.isNotBlank()) {
                    viewModel.addGame(name, date, deck, memo)
                }
            }
            .setNegativeButton(R.string.dialog_record_cancel, null)
            .create()
    }
}

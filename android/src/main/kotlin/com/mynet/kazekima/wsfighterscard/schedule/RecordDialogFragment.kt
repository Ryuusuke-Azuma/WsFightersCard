/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.mynet.kazekima.wsfighterscard.MainViewModel
import com.mynet.kazekima.wsfighterscard.databinding.DialogRecordScheduleBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordDialogFragment : DialogFragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val binding = DialogRecordScheduleBinding.inflate(LayoutInflater.from(context))

        // 今日の日付を初期値として設定
        val today = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
        binding.editGameDate.setText(today)

        return AlertDialog.Builder(context)
            .setTitle("スケジュールの登録")
            .setView(binding.root)
            .setPositiveButton("登録") { _, _ ->
                val name = binding.editGameName.text.toString()
                val date = binding.editGameDate.text.toString()
                val deck = binding.editBattleDeck.text.toString()
                val memo = binding.editMemo.text.toString()

                if (name.isNotBlank()) {
                    viewModel.addGame(name, date, deck, memo)
                }
            }
            .setNegativeButton("キャンセル", null)
            .create()
    }
}

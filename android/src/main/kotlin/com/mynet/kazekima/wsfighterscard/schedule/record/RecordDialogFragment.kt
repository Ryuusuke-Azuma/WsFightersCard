/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule.record

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.DialogRecordScheduleBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RecordDialogFragment : DialogFragment() {

    // ダイアログ専用の ViewModel を使用
    private val viewModel: RecordViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val binding = DialogRecordScheduleBinding.inflate(layoutInflater)

        // 今日の日付を初期値として設定
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
                    // 保存処理を実行し、完了後に結果を通知
                    viewModel.addGame(name, date, deck, memo) {
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
        const val REQUEST_KEY = "record_request"
        const val RESULT_SAVED = "result_saved"
    }
}

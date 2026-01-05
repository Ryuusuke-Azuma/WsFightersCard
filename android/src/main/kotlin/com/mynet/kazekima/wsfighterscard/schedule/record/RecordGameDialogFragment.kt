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
import com.mynet.kazekima.wsfighterscard.databinding.DialogRecordGameBinding
import com.mynet.kazekima.wsfighterscard.db.enums.GameStyle
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RecordGameDialogFragment : DialogFragment() {

    private val viewModel: RecordViewModel by viewModels()
    private var _binding: DialogRecordGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogRecordGameBinding.inflate(layoutInflater)

        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        binding.editGameDate.setText(LocalDate.now().format(formatter))

        return AlertDialog.Builder(requireContext())
            .setTitle(com.mynet.kazekima.wsfighterscard.R.string.dialog_record_game)
            .setView(binding.root)
            .setPositiveButton(com.mynet.kazekima.wsfighterscard.R.string.dialog_record_ok) { _, _ ->
                val name = binding.editGameName.text.toString()
                val dateString = binding.editGameDate.text.toString()
                val memo = binding.editMemo.text.toString()
                
                val style = if (binding.radioTrio.isChecked) GameStyle.TEAMS else GameStyle.SINGLES

                if (name.isNotBlank()) {
                    runCatching {
                        val date = LocalDate.parse(dateString, formatter)
                        viewModel.addGame(name, date, style, memo) {
                            setFragmentResult(REQUEST_KEY, Bundle().apply {
                                putBoolean(RESULT_SAVED, true)
                            })
                        }
                    }
                }
            }
            .setNegativeButton(com.mynet.kazekima.wsfighterscard.R.string.dialog_record_cancel, null)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY = "record_game_request"
        const val RESULT_SAVED = "result_saved"
    }
}

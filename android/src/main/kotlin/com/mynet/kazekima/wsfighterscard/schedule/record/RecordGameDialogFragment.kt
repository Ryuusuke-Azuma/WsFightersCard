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

        val gameId = arguments?.getLong(ARG_ID, -1L) ?: -1L
        val initialName = arguments?.getString(ARG_NAME) ?: ""
        val initialDate = arguments?.getString(ARG_DATE) ?: LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        val initialStyleId = arguments?.getLong(ARG_STYLE, 0L) ?: 0L
        val initialMemo = arguments?.getString(ARG_MEMO) ?: ""

        binding.editGameName.setText(initialName)
        binding.editGameDate.setText(initialDate)
        binding.editMemo.setText(initialMemo)
        if (initialStyleId == GameStyle.TEAMS.id) {
            binding.radioTrio.isChecked = true
        } else {
            binding.radioNeos.isChecked = true
        }

        val isEdit = gameId != -1L

        return AlertDialog.Builder(requireContext())
            .setTitle(if (isEdit) R.string.dialog_edit_game else R.string.dialog_record_game)
            .setView(binding.root)
            .setPositiveButton(if (isEdit) R.string.dialog_edit_ok else R.string.dialog_record_ok) { _, _ ->
                val name = binding.editGameName.text.toString()
                val dateString = binding.editGameDate.text.toString()
                val memo = binding.editMemo.text.toString()
                val style = if (binding.radioTrio.isChecked) GameStyle.TEAMS else GameStyle.SINGLES

                if (name.isNotBlank()) {
                    runCatching {
                        val date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                        if (gameId == -1L) {
                            viewModel.addGame(name, date, style, memo) { notifySaved() }
                        } else {
                            viewModel.updateGame(gameId, name, date, style, memo) { notifySaved() }
                        }
                    }
                }
            }
            .setNegativeButton(if (isEdit) R.string.dialog_edit_cancel else R.string.dialog_record_cancel, null)
            .create()
    }

    private fun notifySaved() {
        setFragmentResult(REQUEST_KEY, Bundle().apply { putBoolean(RESULT_SAVED, true) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY = "record_game_request"
        const val RESULT_SAVED = "result_saved"
        private const val ARG_ID = "arg_id"
        private const val ARG_NAME = "arg_name"
        private const val ARG_DATE = "arg_date"
        private const val ARG_STYLE = "arg_style"
        private const val ARG_MEMO = "arg_memo"

        fun newInstance(date: LocalDate): RecordGameDialogFragment {
            return RecordGameDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DATE, date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                }
            }
        }

        fun newInstanceForEdit(id: Long, name: String, dateStr: String, styleId: Long, memo: String): RecordGameDialogFragment {
            return RecordGameDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_ID, id)
                    putString(ARG_NAME, name)
                    putString(ARG_DATE, dateStr)
                    putLong(ARG_STYLE, styleId)
                    putString(ARG_MEMO, memo)
                }
            }
        }
    }
}

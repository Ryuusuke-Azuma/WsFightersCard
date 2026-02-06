/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule.record

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.DialogRecordGameBinding
import com.mynet.kazekima.wsfighterscard.db.enums.GameStyle
import com.mynet.kazekima.wsfighterscard.schedule.GamesViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RecordGameDialogFragment : DialogFragment() {

    private val viewModel: GamesViewModel by activityViewModels()
    private var _binding: DialogRecordGameBinding? = null
    private val binding get() = _binding!!

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogRecordGameBinding.inflate(layoutInflater)

        val gameId = arguments?.getLong(ARG_ID, -1L) ?: -1L
        val initialName = arguments?.getString(ARG_NAME) ?: ""
        val initialDate = arguments?.getString(ARG_DATE) ?: LocalDate.now().format(dateFormatter)
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

        binding.editGameDate.isFocusable = false
        binding.editGameDate.isClickable = true
        binding.editGameDate.setOnClickListener {
            val currentDate = LocalDate.parse(binding.editGameDate.text.toString(), dateFormatter)

            val dialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    binding.editGameDate.setText(selectedDate.format(dateFormatter))
                },
                currentDate.year,
                currentDate.monthValue - 1,
                currentDate.dayOfMonth
            )
            dialog.show()
        }

        val isEdit = gameId != -1L
        val title = if (isEdit) R.string.dialog_edit_game else R.string.dialog_record_game

        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(binding.root)
            .setPositiveButton(if (isEdit) R.string.dialog_edit_ok else R.string.dialog_record_ok) { _, _ ->
                val name = binding.editGameName.text.toString()
                val dateString = binding.editGameDate.text.toString()
                val memo = binding.editMemo.text.toString()
                val style = if (binding.radioTrio.isChecked) GameStyle.TEAMS else GameStyle.SINGLES

                if (name.isNotBlank()) {
                    val date = LocalDate.parse(dateString, dateFormatter)
                    val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    if (gameId == -1L) {
                        viewModel.addGame(name, millis, style, memo)
                    } else {
                        viewModel.updateGame(gameId, name, millis, style, memo)
                    }
                    parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(RESULT_SAVED to true))
                }
            }
            .setNegativeButton(if (isEdit) R.string.dialog_edit_cancel else R.string.dialog_record_cancel, null)
            .create()
    }

    companion object {
        const val REQUEST_KEY = "RecordGameDialogFragmentRequest"
        const val RESULT_SAVED = "result_saved"
        private const val ARG_ID = "arg_id"
        private const val ARG_NAME = "arg_name"
        private const val ARG_DATE = "arg_date"
        private const val ARG_STYLE = "arg_style"
        private const val ARG_MEMO = "arg_memo"

        fun newInstance(dateStr: String): RecordGameDialogFragment {
            return RecordGameDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DATE, dateStr)
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

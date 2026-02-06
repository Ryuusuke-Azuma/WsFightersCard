/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mynet.kazekima.wsfighterscard.databinding.BottomSheetScheduleBinding

class ScheduleBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetScheduleBinding? = null
    private val binding get() = _binding!!

    private val itemId: Long by lazy { requireArguments().getLong(ITEM_ID) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textBottomSheetScheduleEdit.setOnClickListener {
            setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY to ACTION_EDIT, ITEM_ID to itemId))
            dismiss()
        }

        binding.textBottomSheetScheduleDelete.setOnClickListener {
            setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY to ACTION_DELETE, ITEM_ID to itemId))
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY = "ScheduleBottomSheetRequest"
        const val RESULT_KEY = "ScheduleBottomSheetResult"
        const val ACTION_EDIT = "action_edit"
        const val ACTION_DELETE = "action_delete"
        const val ITEM_ID = "item_id"

        fun newInstance(itemId: Long): ScheduleBottomSheet {
            return ScheduleBottomSheet().apply {
                arguments = bundleOf(ITEM_ID to itemId)
            }
        }
    }
}

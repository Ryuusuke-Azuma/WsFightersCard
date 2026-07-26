/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mynet.kazekima.wsfighterscard.databinding.BottomSheetProfileBinding

class ProfileBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetProfileBinding? = null
    private val binding get() = _binding!!

    private val itemId: Long by lazy { requireArguments().getLong(ITEM_ID) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textBottomSheetProfileEdit.setOnClickListener {
            setFragmentResult(REQUEST_KEY, Bundle().apply {
                putString(RESULT_KEY, ACTION_EDIT)
                putLong(ITEM_ID, itemId)
            })
            dismiss()
        }

        binding.textBottomSheetProfileDelete.setOnClickListener {
            setFragmentResult(REQUEST_KEY, Bundle().apply {
                putString(RESULT_KEY, ACTION_DELETE)
                putLong(ITEM_ID, itemId)
            })
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY = "ProfileBottomSheetRequest"
        const val RESULT_KEY = "ProfileBottomSheetResult"
        const val ACTION_EDIT = "action_edit"
        const val ACTION_DELETE = "action_delete"
        const val ITEM_ID = "item_id"

        fun newInstance(itemId: Long): ProfileBottomSheet {
            return ProfileBottomSheet().apply {
                arguments = Bundle().apply {
                    putLong(ITEM_ID, itemId)
                }
            }
        }
    }
}

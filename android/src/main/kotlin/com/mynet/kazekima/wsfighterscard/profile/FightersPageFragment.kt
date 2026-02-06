/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.ListitemFighterBinding
import com.mynet.kazekima.wsfighterscard.databinding.PageProfileFightersBinding
import com.mynet.kazekima.wsfighterscard.db.Fighter
import com.mynet.kazekima.wsfighterscard.profile.record.DeleteFighterDialogFragment
import com.mynet.kazekima.wsfighterscard.profile.record.RecordFighterDialogFragment

class FightersPageFragment : Fragment() {

    private var _binding: PageProfileFightersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FightersViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = PageProfileFightersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FightersListAdapter(
            onItemClick = { viewModel.selectFighter(it) },
            onMoreClick = { showProfileBottomSheet(it) }
        )
        binding.recyclerViewFighters.adapter = adapter
        viewModel.fighters.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        childFragmentManager.setFragmentResultListener(ProfileBottomSheet.REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            val result = bundle.getString(ProfileBottomSheet.RESULT_KEY)
            val itemId = bundle.getLong(ProfileBottomSheet.ITEM_ID)
            val item = viewModel.fighters.value?.find { it.id == itemId } ?: return@setFragmentResultListener

            when (result) {
                ProfileBottomSheet.ACTION_EDIT -> {
                    RecordFighterDialogFragment.newInstanceForEdit(item.id, item.name, item.memo)
                        .show(childFragmentManager, RecordFighterDialogFragment.REQUEST_KEY)
                }
                ProfileBottomSheet.ACTION_DELETE -> {
                    DeleteFighterDialogFragment.newInstance(item.id, item.name)
                        .show(childFragmentManager, DeleteFighterDialogFragment.REQUEST_KEY)
                }
            }
        }

        viewModel.loadInitialFighters()
    }

    fun showAddDialog() {
        RecordFighterDialogFragment.newInstance()
            .show(childFragmentManager, RecordFighterDialogFragment.REQUEST_KEY)
    }

    private fun showProfileBottomSheet(item: Fighter) {
        ProfileBottomSheet.newInstance(item.id)
            .show(childFragmentManager, ProfileBottomSheet.REQUEST_KEY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class FightersListAdapter(
        private val onItemClick: (Fighter) -> Unit,
        private val onMoreClick: (Fighter) -> Unit
    ) : ListAdapter<Fighter, FightersListAdapter.ViewHolder>(DiffCallback) {

        class ViewHolder(val binding: ListitemFighterBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ListitemFighterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)
            with(holder.binding) {
                root.setOnClickListener { onItemClick(item) }
                listHeader.headerText.text = root.context.getString(R.string.profile_tab_fighters)
                itemTitle.text = item.name
                itemMemo.text = item.memo
                listHeader.btnMore.setOnClickListener { onMoreClick(item) }
                listHeader.btnMore.visibility = View.VISIBLE // Explicitly set to visible
            }
        }

        companion object {
            private val DiffCallback = object : DiffUtil.ItemCallback<Fighter>() {
                override fun areItemsTheSame(old: Fighter, new: Fighter) = old.id == new.id
                override fun areContentsTheSame(old: Fighter, new: Fighter) = old == new
            }
        }
    }
}

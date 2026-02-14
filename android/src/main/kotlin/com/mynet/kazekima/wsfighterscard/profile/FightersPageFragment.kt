/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import com.mynet.kazekima.wsfighterscard.profile.record.SetSelfFighterDialogFragment

class FightersPageFragment : Fragment() {

    private var _binding: PageProfileFightersBinding? = null
    private val binding get() = _binding!!

    private val fightersViewModel: FightersViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = PageProfileFightersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FightersListAdapter(
            onItemClick = { 
                fightersViewModel.selectFighter(it)
                (parentFragment as? ProfileFragment)?.setCurrentPage(1)
            },
            onItemLongClick = { showProfileBottomSheet(it) },
            onStarClick = { showSetSelfDialog(it) },
            onMoreClick = { /* do nothing */ }
        )
        binding.recyclerProfileFighters.adapter = adapter
        fightersViewModel.fighters.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        childFragmentManager.setFragmentResultListener(ProfileBottomSheet.REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            val result = bundle.getString(ProfileBottomSheet.RESULT_KEY)
            val itemId = bundle.getLong(ProfileBottomSheet.ITEM_ID)
            val item = fightersViewModel.fighters.value?.find { it.id == itemId } ?: return@setFragmentResultListener

            when (result) {
                ProfileBottomSheet.ACTION_EDIT -> {
                    RecordFighterDialogFragment.newInstanceForEdit(item.id, item.name, item.is_self, item.memo)
                        .show(childFragmentManager, RecordFighterDialogFragment.REQUEST_KEY)
                }
                ProfileBottomSheet.ACTION_DELETE -> {
                    DeleteFighterDialogFragment.newInstance(item.id, item.name)
                        .show(childFragmentManager, DeleteFighterDialogFragment.REQUEST_KEY)
                }
            }
        }

        childFragmentManager.setFragmentResultListener(SetSelfFighterDialogFragment.REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            val fighterId = bundle.getLong(SetSelfFighterDialogFragment.RESULT_OK)
            val fighter = fightersViewModel.fighters.value?.find { it.id == fighterId } ?: return@setFragmentResultListener
            fightersViewModel.setAsSelf(fighter)
        }

        fightersViewModel.loadInitialFighters()
    }

    fun showAddDialog() {
        RecordFighterDialogFragment.newInstance()
            .show(childFragmentManager, RecordFighterDialogFragment.REQUEST_KEY)
    }

    private fun showSetSelfDialog(fighter: Fighter) {
        SetSelfFighterDialogFragment.newInstance(fighter.id, fighter.name)
            .show(childFragmentManager, SetSelfFighterDialogFragment.REQUEST_KEY)
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
        private val onItemLongClick: (Fighter) -> Unit,
        private val onStarClick: (Fighter) -> Unit,
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
                root.setOnLongClickListener {
                    onItemLongClick(item)
                    true
                }
                includeListitemHeader.textListitemHeader.text = root.context.getString(R.string.profile_tab_fighters)
                textFighterName.text = item.name
                textFighterMemo.text = item.memo

                val starIcon = if (item.is_self == 1L) R.drawable.ic_star_filled else R.drawable.ic_star_border
                val starTint = if (item.is_self == 1L) R.color.yellow_500 else R.color.gray_500
                includeListitemHeader.buttonListitemStar.setImageResource(starIcon)
                includeListitemHeader.buttonListitemStar.setColorFilter(ContextCompat.getColor(root.context, starTint))
                includeListitemHeader.buttonListitemStar.setOnClickListener { onStarClick(item) }
                includeListitemHeader.buttonListitemStar.visibility = View.VISIBLE

                includeListitemHeader.buttonListitemMore.setOnClickListener { onMoreClick(item) }
                includeListitemHeader.buttonListitemMore.visibility = View.VISIBLE
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

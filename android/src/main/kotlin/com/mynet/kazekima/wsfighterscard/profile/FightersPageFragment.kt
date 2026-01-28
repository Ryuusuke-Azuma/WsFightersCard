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
import com.mynet.kazekima.wsfighterscard.databinding.ListitemFighterBinding
import com.mynet.kazekima.wsfighterscard.databinding.PageProfileFightersBinding
import com.mynet.kazekima.wsfighterscard.db.Fighter
import com.mynet.kazekima.wsfighterscard.profile.record.RecordFighterDialogFragment

class FightersPageFragment : Fragment() {

    private var _binding: PageProfileFightersBinding? = null
    private val binding get() = _binding!!

    private val fightersViewModel: FightersViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PageProfileFightersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FighterListAdapter { fighter ->
            fightersViewModel.selectFighter(fighter)
        }
        binding.recyclerViewFighters.adapter = adapter

        fightersViewModel.fighters.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        childFragmentManager.setFragmentResultListener(RecordFighterDialogFragment.REQUEST_KEY, viewLifecycleOwner) { _, b ->
            if (b.getBoolean(RecordFighterDialogFragment.RESULT_SAVED)) {
                fightersViewModel.loadFighters()
            }
        }

        fightersViewModel.loadFighters()
    }

    fun showAddDialog() {
        RecordFighterDialogFragment.newInstance().show(childFragmentManager, RecordFighterDialogFragment.REQUEST_KEY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class FighterListAdapter(private val onItemClick: (Fighter) -> Unit) :
        ListAdapter<Fighter, FighterListAdapter.ViewHolder>(DiffCallback) {

        class ViewHolder(val binding: ListitemFighterBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ListitemFighterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val fighter = getItem(position)
            holder.binding.textFighterName.text = fighter.name
            holder.itemView.setOnClickListener { onItemClick(fighter) }
        }

        companion object {
            private val DiffCallback = object : DiffUtil.ItemCallback<Fighter>() {
                override fun areItemsTheSame(oldItem: Fighter, newItem: Fighter): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Fighter, newItem: Fighter): Boolean {
                    return oldItem == newItem
                }
            }
        }
    }
}

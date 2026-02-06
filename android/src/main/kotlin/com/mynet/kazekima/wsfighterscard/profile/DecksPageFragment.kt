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
import com.mynet.kazekima.wsfighterscard.databinding.ListitemDeckBinding
import com.mynet.kazekima.wsfighterscard.databinding.PageProfileDecksBinding
import com.mynet.kazekima.wsfighterscard.db.Deck
import com.mynet.kazekima.wsfighterscard.profile.record.DeleteDeckDialogFragment
import com.mynet.kazekima.wsfighterscard.profile.record.RecordDeckDialogFragment

class DecksPageFragment : Fragment() {

    private var _binding: PageProfileDecksBinding? = null
    private val binding get() = _binding!!

    private val fightersViewModel: FightersViewModel by activityViewModels()
    private val decksViewModel: DecksViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = PageProfileDecksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = DecksListAdapter {
            showProfileBottomSheet(it)
        }
        binding.recyclerViewDecks.adapter = adapter
        decksViewModel.decks.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        fightersViewModel.selectedFighter.observe(viewLifecycleOwner) { fighter ->
            if (fighter != null) {
                decksViewModel.loadInitialDecksForFighter(fighter.id)
            } else {
                adapter.submitList(emptyList())
            }
        }

        childFragmentManager.setFragmentResultListener(ProfileBottomSheet.REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            val result = bundle.getString(ProfileBottomSheet.RESULT_KEY)
            val itemId = bundle.getLong(ProfileBottomSheet.ITEM_ID)
            val item = decksViewModel.decks.value?.find { it.id == itemId } ?: return@setFragmentResultListener

            when (result) {
                ProfileBottomSheet.ACTION_EDIT -> {
                    RecordDeckDialogFragment.newInstanceForEdit(item.id, item.deck_name, item.memo)
                        .show(childFragmentManager, RecordDeckDialogFragment.REQUEST_KEY)
                }
                ProfileBottomSheet.ACTION_DELETE -> {
                    DeleteDeckDialogFragment.newInstance(item.id, item.deck_name)
                        .show(childFragmentManager, DeleteDeckDialogFragment.REQUEST_KEY)
                }
            }
        }
    }

    fun showAddDialog() {
        fightersViewModel.selectedFighter.value?.let {
            RecordDeckDialogFragment.newInstance(it.id)
                .show(childFragmentManager, RecordDeckDialogFragment.REQUEST_KEY)
        }
    }

    private fun showProfileBottomSheet(item: Deck) {
        ProfileBottomSheet.newInstance(item.id)
            .show(childFragmentManager, ProfileBottomSheet.REQUEST_KEY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class DecksListAdapter(private val onMoreClick: (Deck) -> Unit) :
        ListAdapter<Deck, DecksListAdapter.ViewHolder>(DiffCallback) {

        class ViewHolder(val binding: ListitemDeckBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ListitemDeckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)
            with(holder.binding) {
                listHeader.headerText.text = root.context.getString(R.string.profile_tab_decks)
                itemTitle.text = item.deck_name
                itemMemo.text = item.memo
                listHeader.btnMore.setOnClickListener { onMoreClick(item) }
                listHeader.btnMore.visibility = View.VISIBLE // Explicitly set to visible
            }
        }

        companion object {
            private val DiffCallback = object : DiffUtil.ItemCallback<Deck>() {
                override fun areItemsTheSame(old: Deck, new: Deck) = old.id == new.id
                override fun areContentsTheSame(old: Deck, new: Deck) = old == new
            }
        }
    }
}

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
import com.mynet.kazekima.wsfighterscard.databinding.ListitemDeckBinding
import com.mynet.kazekima.wsfighterscard.databinding.PageProfileDecksBinding
import com.mynet.kazekima.wsfighterscard.db.Deck
import com.mynet.kazekima.wsfighterscard.profile.record.RecordDeckDialogFragment

class DecksPageFragment : Fragment() {

    private var _binding: PageProfileDecksBinding? = null
    private val binding get() = _binding!!

    private val fightersViewModel: FightersViewModel by activityViewModels()
    private val decksViewModel: DecksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PageProfileDecksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = DeckListAdapter()
        binding.recyclerViewDecks.adapter = adapter

        fightersViewModel.selectedFighter.observe(viewLifecycleOwner) { fighter ->
            if (fighter != null) {
                decksViewModel.loadDecks(fighter.id)
            } else {
                adapter.submitList(emptyList())
            }
        }

        decksViewModel.decks.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        childFragmentManager.setFragmentResultListener(RecordDeckDialogFragment.REQUEST_KEY, viewLifecycleOwner) { _, b ->
            if (b.getBoolean(RecordDeckDialogFragment.RESULT_SAVED)) {
                fightersViewModel.selectedFighter.value?.let { decksViewModel.loadDecks(it.id) }
            }
        }
    }

    fun showAddDialog() {
        val fighter = fightersViewModel.selectedFighter.value ?: return
        RecordDeckDialogFragment.newInstance(fighter.id).show(childFragmentManager, RecordDeckDialogFragment.REQUEST_KEY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class DeckListAdapter :
        ListAdapter<Deck, DeckListAdapter.ViewHolder>(DiffCallback) {

        class ViewHolder(val binding: ListitemDeckBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ListitemDeckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val deck = getItem(position)
            holder.binding.textDeckName.text = deck.deck_name
        }

        companion object {
            private val DiffCallback = object : DiffUtil.ItemCallback<Deck>() {
                override fun areItemsTheSame(oldItem: Deck, newItem: Deck): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Deck, newItem: Deck): Boolean {
                    return oldItem == newItem
                }
            }
        }
    }
}

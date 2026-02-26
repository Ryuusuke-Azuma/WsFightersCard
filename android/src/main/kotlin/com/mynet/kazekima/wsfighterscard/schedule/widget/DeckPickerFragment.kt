/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule.widget

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mynet.kazekima.wsfighterscard.db.Deck
import com.mynet.kazekima.wsfighterscard.databinding.DialogWidgetDeckBinding
import com.mynet.kazekima.wsfighterscard.schedule.DeckPickerViewModel
import com.mynet.kazekima.wsfighterscard.schedule.models.FighterItem
import kotlinx.coroutines.launch

class DeckPickerFragment : DialogFragment() {

    private val viewModel: DeckPickerViewModel by viewModels()
    private var _binding: DialogWidgetDeckBinding? = null
    private val binding get() = _binding!!

    private var isMyDeckSelection: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isMyDeckSelection = arguments?.getBoolean(ARG_IS_MY_DECK) ?: true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogWidgetDeckBinding.inflate(layoutInflater)

        val deckAdapter = DeckAdapter { deck ->
            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(
                RESULT_IS_MY_DECK to isMyDeckSelection,
                RESULT_DECK_NAME to deck.deck_name
            ))
            dismiss()
        }
        binding.recyclerViewDecks.adapter = deckAdapter
        binding.recyclerViewDecks.layoutManager = LinearLayoutManager(context)

        val spinnerAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFighters.adapter = spinnerAdapter

        val passedFighters = arguments?.let {
            BundleCompat.getParcelableArrayList(it, ARG_FIGHTERS, FighterItem::class.java)
        }

        if (passedFighters != null) {
            setupSpinner(passedFighters, spinnerAdapter, deckAdapter)
        } else {
            lifecycleScope.launch {
                val fighters = viewModel.getFighters(isMyDeckSelection).map { FighterItem(it.id, it.name) }
                setupSpinner(fighters, spinnerAdapter, deckAdapter)
            }
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(if (isMyDeckSelection) "My Deck" else "Opponent Deck")
            .setView(binding.root)
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

    private fun setupSpinner(fighters: List<FighterItem>, adapter: ArrayAdapter<String>, deckAdapter: DeckAdapter) {
        adapter.clear()
        adapter.addAll(fighters.map { it.name })

        binding.spinnerFighters.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val fighterId = fighters.getOrNull(position)?.id ?: return
                lifecycleScope.launch {
                    val decks = viewModel.getDecksForFighter(fighterId)
                    deckAdapter.submitList(decks)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { /* Do nothing */ }
        }
    }

    companion object {
        const val REQUEST_KEY = "DeckPickerRequest"
        const val RESULT_DECK_NAME = "result_deck_name"
        const val RESULT_IS_MY_DECK = "result_is_my_deck"
        private const val ARG_IS_MY_DECK = "is_my_deck"
        private const val ARG_FIGHTERS = "fighters"

        fun newInstance(isMyDeck: Boolean, fighters: List<FighterItem>? = null): DeckPickerFragment {
            return DeckPickerFragment().apply {
                arguments = bundleOf(
                    ARG_IS_MY_DECK to isMyDeck,
                    ARG_FIGHTERS to (fighters?.let { ArrayList(it) })
                )
            }
        }
    }

    private class DeckAdapter(private val onItemClick: (Deck) -> Unit) : ListAdapter<Deck, DeckAdapter.ViewHolder>(DiffCallback) {

        class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
            fun bind(deck: Deck) {
                (view as? android.widget.TextView)?.text = deck.deck_name
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val deck = getItem(position)
            holder.bind(deck)
            holder.itemView.setOnClickListener { onItemClick(deck) }
        }

        companion object {
            private val DiffCallback = object : DiffUtil.ItemCallback<Deck>() {
                override fun areItemsTheSame(oldItem: Deck, newItem: Deck): Boolean = oldItem.id == newItem.id
                override fun areContentsTheSame(oldItem: Deck, newItem: Deck): Boolean = oldItem == newItem
            }
        }
    }
}

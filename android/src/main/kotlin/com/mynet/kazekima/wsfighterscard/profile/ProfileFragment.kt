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
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val fightersViewModel: FightersViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = ProfilePagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) getString(R.string.profile_tab_fighters) else getString(R.string.profile_tab_decks)
        }.attach()

        setupFightersTab()
        setupFab()
    }

    private fun setupFightersTab() {
        fightersViewModel.selectedFighter.observe(viewLifecycleOwner) { fighter ->
            if (fighter != null) {
                binding.viewPager.currentItem = 1
            }
        }
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            val fragment = childFragmentManager.fragments.getOrNull(currentItem)
            if (fragment is FightersPageFragment) {
                fragment.showAddDialog()
            } else if (fragment is DecksPageFragment) {
                fragment.showAddDialog()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class ProfilePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return if (position == 0) FightersPageFragment() else DecksPageFragment()
        }
    }
}

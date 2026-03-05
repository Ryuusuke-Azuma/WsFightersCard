/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.FragmentHelpBinding
import com.mynet.kazekima.wsfighterscard.databinding.ItemHelpPageBinding

class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pages = listOf(
            HelpPage(R.drawable.help_add_circle, R.string.help_page_add_game_title, R.string.help_page_add_game_desc),
            HelpPage(R.drawable.help_touch_app, R.string.help_page_edit_delete_title, R.string.help_page_edit_delete_desc),
            HelpPage(R.drawable.help_share_self, R.string.help_page_share_title, R.string.help_page_share_desc),
            HelpPage(R.drawable.help_account_box, R.string.help_page_profile_title, R.string.help_page_profile_desc),
            HelpPage(R.drawable.help_star, R.string.help_page_self_setting_title, R.string.help_page_self_setting_desc),
            HelpPage(R.drawable.help_assessment, R.string.help_page_analytics_title, R.string.help_page_analytics_desc),
            HelpPage(R.drawable.help_sync_alt, R.string.help_page_data_transfer_title, R.string.help_page_data_transfer_desc)
        )

        val adapter = HelpPagerAdapter(pages)
        binding.pagerHelp.adapter = adapter

        TabLayoutMediator(binding.indicatorHelp, binding.pagerHelp) { _, _ -> }.attach()

        binding.buttonHelpNext.setOnClickListener {
            val current = binding.pagerHelp.currentItem
            if (current < pages.size - 1) {
                binding.pagerHelp.currentItem = current + 1
            } else {
                parentFragmentManager.popBackStack()
            }
        }

        binding.buttonHelpClose.setOnClickListener {
            val current = binding.pagerHelp.currentItem
            if (current > 0) {
                binding.pagerHelp.currentItem = current - 1
            }
        }

        binding.pagerHelp.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    binding.buttonHelpClose.visibility = View.INVISIBLE
                } else {
                    binding.buttonHelpClose.visibility = View.VISIBLE
                    binding.buttonHelpClose.text = getString(R.string.help_page_prev)
                }

                binding.buttonHelpNext.text = if (position == pages.size - 1) {
                    getString(R.string.help_page_close)
                } else {
                    getString(R.string.help_page_next)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class HelpPage(val imageRes: Int, val titleRes: Int, val descriptionRes: Int)

    private inner class HelpPagerAdapter(private val pages: List<HelpPage>) : RecyclerView.Adapter<HelpPagerAdapter.ViewHolder>() {
        inner class ViewHolder(val binding: ItemHelpPageBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemHelpPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val page = pages[position]
            holder.binding.imageHelpPage.setImageResource(page.imageRes)
            holder.binding.textHelpTitle.setText(page.titleRes)
            holder.binding.textHelpDescription.setText(page.descriptionRes)
        }

        override fun getItemCount(): Int = pages.size
    }
}

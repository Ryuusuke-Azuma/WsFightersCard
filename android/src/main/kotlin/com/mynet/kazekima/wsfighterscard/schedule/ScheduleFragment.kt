/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.FragmentScheduleBinding
import com.mynet.kazekima.wsfighterscard.schedule.widget.CalendarPickerFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private val scheduleViewModel: ScheduleViewModel by activityViewModels()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCalendarPicker()

        binding.pagerSchedule.adapter = SchedulePagerAdapter(this)
        TabLayoutMediator(binding.tabsSchedule, binding.pagerSchedule) { tab, position ->
            tab.text = if (position == 0) getString(R.string.schedule_tab_games) else getString(R.string.schedule_tab_scores)
        }.attach()

        setupFab()
        setupMenu()
    }

    private fun setupCalendarPicker() {
        scheduleViewModel.selectedDate.observe(viewLifecycleOwner) {
            binding.buttonScheduleDate.text = it.format(dateFormatter)
            binding.pagerSchedule.currentItem = 0
        }

        binding.buttonScheduleDate.setOnClickListener {
            scheduleViewModel.loadInitialSchedule()
            CalendarPickerFragment.newInstance().show(childFragmentManager, CalendarPickerFragment.REQUEST_KEY)
        }

        childFragmentManager.setFragmentResultListener(CalendarPickerFragment.REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            val result = bundle.getLong(CalendarPickerFragment.RESULT_DATE)
            scheduleViewModel.setSelectedDate(LocalDate.ofEpochDay(result))
        }
    }

    private fun setupFab() {
        binding.fabSchedule.setOnClickListener {
            val currentItem = binding.pagerSchedule.currentItem
            val fragment = childFragmentManager.fragments.getOrNull(currentItem)
            if (fragment is GamesPageFragment) {
                fragment.showAddDialog()
            } else if (fragment is ScoresPageFragment) {
                fragment.showAddDialog()
            }
        }
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_schedule, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.schedule_action_today) {
                    scheduleViewModel.setSelectedDate(LocalDate.now())
                    return true
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class SchedulePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return if (position == 0) GamesPageFragment() else ScoresPageFragment()
        }
    }
}

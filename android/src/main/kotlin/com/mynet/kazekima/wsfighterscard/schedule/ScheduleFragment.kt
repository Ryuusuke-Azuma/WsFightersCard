/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.FragmentScheduleBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.time.LocalDate

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    val binding get() = _binding!!

    private val scheduleViewModel: ScheduleViewModel by activityViewModels()
    private val gamesViewModel: GamesViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCalendar()

        binding.viewPager.adapter = SchedulePagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) getString(R.string.schedule_tab_games) else getString(R.string.schedule_tab_scores)
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateFabIcon()
                if (position == 0) {
                    gamesViewModel.selectGame(null)
                }
            }
        })

        setupFab()
        setupMenu()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupCalendar() {
        binding.calendarView.selectionMode = MaterialCalendarView.SELECTION_MODE_SINGLE
        val initialDate = scheduleViewModel.selectedDate.value ?: LocalDate.now()
        binding.calendarView.setSelectedDate(CalendarDay.from(initialDate.year, initialDate.monthValue, initialDate.dayOfMonth))
        binding.calendarView.setOnDateChangedListener { _, day, selected ->
            if (selected) {
                scheduleViewModel.setSelectedDate(LocalDate.of(day.year, day.month, day.day))
            }
        }
        scheduleViewModel.selectedDate.observe(viewLifecycleOwner) {
            updateDecorators(scheduleViewModel.markedDates.value ?: emptyList())
            binding.viewPager.currentItem = 0
        }
        scheduleViewModel.markedDates.observe(viewLifecycleOwner) { dates -> updateDecorators(dates) }
        scheduleViewModel.loadData()
    }

    private fun updateDecorators(markedDates: List<LocalDate>) {
        binding.calendarView.removeDecorators()
        val context = requireContext()
        val selectedDay = scheduleViewModel.selectedDate.value?.let { CalendarDay.from(it.year, it.monthValue, it.dayOfMonth) } ?: CalendarDay.today()
        val dotColor = ContextCompat.getColor(context, R.color.calendar_event_dot)
        binding.calendarView.addDecorator(TodayDecorator(context, selectedDay))
        binding.calendarView.addDecorator(SelectionDecorator(context, selectedDay))
        if (markedDates.isNotEmpty()) binding.calendarView.addDecorator(EventDecorator(dotColor, markedDates))
        binding.calendarView.invalidateDecorators()
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            val fragment = childFragmentManager.fragments.getOrNull(currentItem)
            if (fragment is GamesPageFragment) {
                fragment.showAddDialog()
            } else if (fragment is ScoresPageFragment) {
                fragment.showAddDialog()
            }
        }
        updateFabIcon()
    }

    private fun updateFabIcon() {
        val isGamesTab = binding.viewPager.currentItem == 0
        val descRes = if (isGamesTab) R.string.dialog_record_game else R.string.dialog_record_score
        binding.fab.contentDescription = getString(descRes)
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_schedule, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.schedule_action_today) {
                    scrollToToday()
                    return true
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun scrollToToday() {
        val today = CalendarDay.today()
        binding.calendarView.setSelectedDate(today)
        binding.calendarView.setCurrentDate(today)
        scheduleViewModel.setSelectedDate(LocalDate.now())
    }

    private class SchedulePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int): Fragment = if (position == 0) GamesPageFragment() else ScoresPageFragment()
    }

    class TodayDecorator(context: Context, private val selectedDay: CalendarDay) : DayViewDecorator {
        private val today = CalendarDay.today()
        private val drawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.today_circle)
        override fun shouldDecorate(day: CalendarDay): Boolean = day == today && day != selectedDay
        override fun decorate(view: DayViewFacade) { drawable?.let { view.setBackgroundDrawable(it) }; view.addSpan(StyleSpan(Typeface.BOLD)) }
    }

    class SelectionDecorator(context: Context, private val selectedDay: CalendarDay) : DayViewDecorator {
        private val drawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.selected_circle)
        override fun shouldDecorate(day: CalendarDay): Boolean = day == selectedDay
        override fun decorate(view: DayViewFacade) { drawable?.let { view.setSelectionDrawable(it) } }
    }

    class EventDecorator(private val color: Int, dates: List<LocalDate>) : DayViewDecorator {
        private val calendarDays = dates.map { CalendarDay.from(it.year, it.monthValue, it.dayOfMonth) }.toSet()
        override fun shouldDecorate(day: CalendarDay): Boolean = calendarDays.contains(day)
        override fun decorate(view: DayViewFacade) { view.addSpan(DotSpan(5f, color)); view.addSpan(StyleSpan(Typeface.BOLD)) }
    }
}

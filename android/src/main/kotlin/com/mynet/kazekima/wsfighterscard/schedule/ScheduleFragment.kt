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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayoutMediator
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.FragmentScheduleBinding
import com.mynet.kazekima.wsfighterscard.schedule.record.DeleteGameDialogFragment
import com.mynet.kazekima.wsfighterscard.schedule.record.DeleteScoreDialogFragment
import com.mynet.kazekima.wsfighterscard.schedule.record.RecordGameDialogFragment
import com.mynet.kazekima.wsfighterscard.schedule.record.RecordScoreDialogFragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    val binding get() = _binding!!

    private val viewModel: ScheduleViewModel by activityViewModels()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupCalendar()

        val adapter = SchedulePagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) getString(R.string.label_games) else getString(R.string.label_scores)
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setupFab()
            }
        })

        setupFab()

        childFragmentManager.setFragmentResultListener(RecordGameDialogFragment.REQUEST_KEY, viewLifecycleOwner) { _, b ->
            if (b.getBoolean(RecordGameDialogFragment.RESULT_SAVED)) {
                viewModel.loadData()
            }
        }
        childFragmentManager.setFragmentResultListener(RecordScoreDialogFragment.REQUEST_KEY, viewLifecycleOwner) { _, b ->
            if (b.getBoolean(RecordScoreDialogFragment.RESULT_SAVED)) {
                viewModel.loadData()
            }
        }
        childFragmentManager.setFragmentResultListener(DeleteGameDialogFragment.REQUEST_KEY, viewLifecycleOwner) { _, b ->
            if (b.getBoolean(DeleteGameDialogFragment.RESULT_DELETED)) {
                viewModel.loadData()
            }
        }
        childFragmentManager.setFragmentResultListener(DeleteScoreDialogFragment.REQUEST_KEY, viewLifecycleOwner) { _, b ->
            if (b.getBoolean(DeleteScoreDialogFragment.RESULT_DELETED)) {
                viewModel.loadData()
            }
        }

        viewModel.markedDates.observe(viewLifecycleOwner) { dates -> updateDecorators(dates) }
        viewModel.selectedDate.observe(viewLifecycleOwner) {
            viewModel.loadData()
            updateDecorators(viewModel.markedDates.value ?: emptyList())
        }
        viewModel.switchToGamesTab.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                binding.viewPager.currentItem = 0
            }
        }

        viewModel.loadData()
    }

    private fun setupFab() {
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)
        fab?.setOnClickListener {
            val currentPos = binding.viewPager.currentItem
            if (currentPos == 0) {
                val date = viewModel.selectedDate.value ?: LocalDate.now()
                RecordGameDialogFragment.newInstance(date.format(dateFormatter)).show(childFragmentManager, "game")
            } else {
                viewModel.selectedGame.value?.let { item ->
                    RecordScoreDialogFragment.newInstance(item.game.id, item.game.game_name, item.game.game_style.id).show(childFragmentManager, "score")
                } ?: run {
                    Toast.makeText(requireContext(), "Please select a game first", Toast.LENGTH_SHORT).show()
                    binding.viewPager.currentItem = 0
                }
            }
        }
    }

    private fun setupCalendar() {
        binding.calendarView.selectionMode = MaterialCalendarView.SELECTION_MODE_SINGLE
        val initialDate = viewModel.selectedDate.value ?: LocalDate.now()
        binding.calendarView.setSelectedDate(CalendarDay.from(initialDate.year, initialDate.monthValue, initialDate.dayOfMonth))
        binding.calendarView.setOnDateChangedListener { _, day, selected ->
            if (selected) {
                viewModel.setSelectedDate(LocalDate.of(day.year, day.month, day.day))
            }
        }
    }

    private fun updateDecorators(markedDates: List<LocalDate>) {
        binding.calendarView.removeDecorators()
        val context = requireContext()
        val selectedDay = viewModel.selectedDate.value?.let { CalendarDay.from(it.year, it.monthValue, it.dayOfMonth) } ?: CalendarDay.today()
        val dotColor = ContextCompat.getColor(context, R.color.calendar_event_dot)
        binding.calendarView.addDecorator(TodayDecorator(context, selectedDay))
        binding.calendarView.addDecorator(SelectionDecorator(context, selectedDay))
        if (markedDates.isNotEmpty()) binding.calendarView.addDecorator(EventDecorator(dotColor, markedDates))
        binding.calendarView.invalidateDecorators()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_schedule, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.action_today) {
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
        viewModel.setSelectedDate(LocalDate.now())
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }

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

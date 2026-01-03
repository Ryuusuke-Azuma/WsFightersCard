/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule

import android.content.Context
import android.graphics.Color
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
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.FragmentScheduleBinding
import com.mynet.kazekima.wsfighterscard.schedule.record.RecordGameDialogFragment
import com.mynet.kazekima.wsfighterscard.schedule.record.RecordScoreDialogFragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.time.LocalDate

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ScheduleViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupCalendar()

        val adapter = ScheduleListAdapter { item ->
            val scoreDialog = RecordScoreDialogFragment.newInstance(item.id, item.game_name ?: "")
            scoreDialog.show(childFragmentManager, "score")
        }
        binding.recyclerView.adapter = adapter

        setFragmentResultListener(RecordGameDialogFragment.REQUEST_KEY) { _, b -> if (b.getBoolean("result_saved")) viewModel.loadData() }
        setFragmentResultListener(RecordScoreDialogFragment.REQUEST_KEY) { _, b -> if (b.getBoolean("result_saved")) viewModel.loadData() }

        viewModel.games.observe(viewLifecycleOwner) { adapter.submitList(it) }
        
        viewModel.markedDates.observe(viewLifecycleOwner) { dates ->
            updateDecorators(dates)
        }

        // 選択日が変更された時にもデコレーターを更新 (今日と選択円の競合回避のため)
        viewModel.selectedDate.observe(viewLifecycleOwner) {
            updateDecorators(viewModel.markedDates.value ?: emptyList())
        }
        
        viewModel.loadData()
    }

    private fun setupCalendar() {
        binding.calendarView.selectionMode = com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_SINGLE
        binding.calendarView.setSelectedDate(CalendarDay.today())

        binding.calendarView.setOnDateChangedListener { _, day, selected ->
            if (selected) {
                val localDate = LocalDate.of(day.year, day.month, day.day)
                viewModel.setSelectedDate(localDate)
            }
        }
    }

    private fun updateDecorators(markedDates: List<LocalDate>) {
        binding.calendarView.removeDecorators()
        
        val context = requireContext()
        val selectedDay = CalendarDay.from(
            viewModel.selectedDate.value?.year ?: 0,
            viewModel.selectedDate.value?.monthValue ?: 0,
            viewModel.selectedDate.value?.dayOfMonth ?: 0
        )

        // 1. 今日を枠線で囲む (選択されていない時のみ)
        binding.calendarView.addDecorator(TodayDecorator(context, selectedDay))
        
        // 2. 選択された日を塗りつぶす
        binding.calendarView.addDecorator(SelectionDecorator(context, selectedDay))
        
        // 3. 予定がある日を強調
        binding.calendarView.addDecorator(EventDecorator(Color.parseColor("#E91E63"), markedDates))
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_schedule, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_today -> {
                        scrollToToday()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun scrollToToday() {
        val today = CalendarDay.today()
        binding.calendarView.setSelectedDate(today)
        binding.calendarView.setCurrentDate(today)
        viewModel.setSelectedDate(LocalDate.now())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /** 今日を枠線で囲む */
    class TodayDecorator(context: Context, private val selectedDay: CalendarDay) : DayViewDecorator {
        private val today = CalendarDay.today()
        private val drawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.today_circle)

        override fun shouldDecorate(day: CalendarDay): Boolean = day == today && day != selectedDay

        override fun decorate(view: DayViewFacade) {
            drawable?.let { view.setBackgroundDrawable(it) }
            view.addSpan(StyleSpan(Typeface.BOLD))
        }
    }

    /** 選択された日を一回り小さな円で塗りつぶす */
    class SelectionDecorator(context: Context, private val selectedDay: CalendarDay) : DayViewDecorator {
        private val drawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.selected_circle)

        override fun shouldDecorate(day: CalendarDay): Boolean = day == selectedDay

        override fun decorate(view: DayViewFacade) {
            drawable?.let { view.setSelectionDrawable(it) }
        }
    }

    /** 予定がある日のピンクドット */
    class EventDecorator(private val color: Int, dates: List<LocalDate>) : DayViewDecorator {
        private val calendarDays = dates.map { CalendarDay.from(it.year, it.monthValue, it.dayOfMonth) }.toSet()

        override fun shouldDecorate(day: CalendarDay): Boolean = calendarDays.contains(day)

        override fun decorate(view: DayViewFacade) {
            view.addSpan(DotSpan(10f, color))
            view.addSpan(StyleSpan(Typeface.BOLD))
        }
    }
}

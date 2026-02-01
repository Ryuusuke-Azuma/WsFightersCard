/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule.widget

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.schedule.ScheduleViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.time.LocalDate

class CalendarPickerFragment : DialogFragment() {

    private val scheduleViewModel: ScheduleViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_widget_calendar, null)
        val calendarView = dialogView.findViewById<MaterialCalendarView>(R.id.dialogCalendarView)

        val currentDate = scheduleViewModel.selectedDate.value ?: LocalDate.now()
        calendarView.setSelectedDate(CalendarDay.from(currentDate.year, currentDate.monthValue, currentDate.dayOfMonth))
        updateDecorators(calendarView, scheduleViewModel.markedDates.value ?: emptyList())

        calendarView.setOnDateChangedListener { _, day, selected ->
            if (selected) {
                val resultDate = LocalDate.of(day.year, day.month, day.day)
                parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(RESULT_DATE to resultDate.toEpochDay()))
                dismiss()
            }
        }

        return AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
    }

    private fun updateDecorators(calendarView: MaterialCalendarView, markedDates: List<LocalDate>) {
        calendarView.removeDecorators()
        val context = requireContext()
        val selectedDay = scheduleViewModel.selectedDate.value?.let { CalendarDay.from(it.year, it.monthValue, it.dayOfMonth) } ?: CalendarDay.today()
        val dotColor = ContextCompat.getColor(context, R.color.calendar_event_dot)
        calendarView.addDecorator(TodayDecorator(context, selectedDay))
        calendarView.addDecorator(SelectionDecorator(context, selectedDay))
        if (markedDates.isNotEmpty()) calendarView.addDecorator(EventDecorator(dotColor, markedDates))
        calendarView.invalidateDecorators()
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

    companion object {
        const val REQUEST_KEY = "CalendarPickerFragmentRequest"
        const val RESULT_DATE = "result_date"

        fun newInstance(): CalendarPickerFragment {
            return CalendarPickerFragment()
        }
    }
}

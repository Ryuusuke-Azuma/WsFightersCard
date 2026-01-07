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
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayoutMediator
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.*
import com.mynet.kazekima.wsfighterscard.db.Score
import com.mynet.kazekima.wsfighterscard.db.enums.WinLose
import com.mynet.kazekima.wsfighterscard.schedule.models.GameDisplayItem
import com.mynet.kazekima.wsfighterscard.schedule.record.RecordGameDialogFragment
import com.mynet.kazekima.wsfighterscard.schedule.record.RecordScoreDialogFragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    val binding get() = _binding!!
    
    private val viewModel: ScheduleViewModel by activityViewModels()

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
            tab.text = if (position == 0) getString(R.string.menu_schedule) else "Scores"
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setupFab()
            }
        })

        setupFab()

        setFragmentResultListener(RecordGameDialogFragment.REQUEST_KEY) { _, b -> if (b.getBoolean("result_saved")) viewModel.loadData() }
        setFragmentResultListener(RecordScoreDialogFragment.REQUEST_KEY) { _, b -> if (b.getBoolean("result_saved")) viewModel.loadData() }

        viewModel.markedDates.observe(viewLifecycleOwner) { dates -> updateDecorators(dates) }
        viewModel.selectedDate.observe(viewLifecycleOwner) { updateDecorators(viewModel.markedDates.value ?: emptyList()) }
        
        viewModel.loadData()
    }

    private fun setupFab() {
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)
        fab?.setOnClickListener {
            val currentPos = binding.viewPager.currentItem
            if (currentPos == 0) {
                RecordGameDialogFragment.newInstance(viewModel.selectedDate.value ?: LocalDate.now()).show(childFragmentManager, "game")
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
        binding.calendarView.setSelectedDate(CalendarDay.today())
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
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) { menuInflater.inflate(R.menu.menu_schedule, menu) }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.action_today) { scrollToToday(); return true }
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

    class GamesPageFragment : Fragment() {
        private val viewModel: ScheduleViewModel by activityViewModels()
        private var _binding: SchedulePageGamesBinding? = null
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            _binding = SchedulePageGamesBinding.inflate(inflater, container, false)
            return _binding!!.root
        }
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val adapter = ScheduleListAdapter(
                onItemClick = { item ->
                    viewModel.selectGame(item)
                    (parentFragment as? ScheduleFragment)?.binding?.viewPager?.currentItem = 1
                },
                onMoreClick = { view, item -> showItemMenu(view, item) }
            )
            _binding!!.recyclerViewGames.adapter = adapter
            viewModel.games.observe(viewLifecycleOwner) { adapter.submitList(it) }
        }
        private fun showItemMenu(anchor: View, item: GameDisplayItem) {
            val popup = PopupMenu(requireContext(), anchor)
            popup.menu.add("Edit")
            popup.menu.add("Delete")
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    "Edit" -> {
                        val dateStr = Instant.ofEpochMilli(item.game.game_date).atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
                        RecordGameDialogFragment.newInstanceForEdit(item.game.id, item.game.game_name, dateStr, item.game.game_style.id, item.game.memo)
                            .show(childFragmentManager, "edit_game")
                    }
                    "Delete" -> {
                        AlertDialog.Builder(requireContext())
                            .setTitle(R.string.dialog_delete_confirm_title)
                            .setMessage(getString(R.string.dialog_delete_confirm_message, item.game.game_name))
                            .setPositiveButton(R.string.dialog_delete_ok) { _, _ -> viewModel.deleteGame(item.game.id) }
                            .setNegativeButton(R.string.dialog_delete_cancel, null).show()
                    }
                }
                true
            }
            popup.show()
        }
        override fun onDestroyView() { super.onDestroyView(); _binding = null }
    }

    class ScoresPageFragment : Fragment() {
        private val viewModel: ScheduleViewModel by activityViewModels()
        private var _binding: SchedulePageScoresBinding? = null
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            _binding = SchedulePageScoresBinding.inflate(inflater, container, false)
            return _binding!!.root
        }
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val adapter = ScoreListAdapter { view, score -> showScoreMenu(view, score) }
            _binding!!.recyclerViewScores.adapter = adapter
            viewModel.scores.observe(viewLifecycleOwner) {
                adapter.submitList(it)
                _binding!!.recyclerViewScores.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
            }
        }
        private fun showScoreMenu(anchor: View, score: Score) {
            val popup = PopupMenu(requireContext(), anchor)
            popup.menu.add("Edit")
            popup.menu.add("Delete")
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    "Edit" -> {
                        val game = viewModel.selectedGame.value?.game
                        if (game != null) {
                            RecordScoreDialogFragment.newInstanceForEdit(score.id, score.game_id, score.battle_deck, score.matching_deck, score.win_lose.id, score.team_win_lose?.id ?: -1L, score.memo)
                                .show(childFragmentManager, "edit_score")
                        }
                    }
                    "Delete" -> {
                        AlertDialog.Builder(requireContext())
                            .setTitle(R.string.dialog_delete_confirm_title)
                            .setMessage("Delete this match result?")
                            .setPositiveButton(R.string.dialog_delete_ok) { _, _ -> viewModel.deleteScore(score.id) }
                            .setNegativeButton(R.string.dialog_delete_cancel, null).show()
                    }
                }
                true
            }
            popup.show()
        }
        override fun onDestroyView() { super.onDestroyView(); _binding = null }
    }

    private class ScoreListAdapter(private val onMoreClick: (View, Score) -> Unit) : ListAdapter<Score, ScoreListAdapter.ViewHolder>(DiffCallback) {
        class ViewHolder(val binding: ListitemScoreBinding) : RecyclerView.ViewHolder(binding.root)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(ListitemScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)
            with(holder.binding) {
                textMatchIndex.text = "${position + 1}."
                textDecks.text = root.context.getString(R.string.format_match_decks, item.battle_deck, item.matching_deck)
                textResult.text = item.win_lose.label
                if (item.team_win_lose != null) {
                    val labelId = if (item.team_win_lose!!.winLose == WinLose.WIN) R.string.label_team_win else R.string.label_team_lose
                    textResult.text = root.context.getString(R.string.format_team_match_result, item.win_lose.label, item.team_win_lose!!.label, root.context.getString(labelId))
                }
                textMemo.text = item.memo
                textMemo.visibility = if (item.memo.isNotBlank()) View.VISIBLE else View.GONE
                btnMore.setOnClickListener { onMoreClick(it, item) }
            }
        }
        companion object {
            private val DiffCallback = object : DiffUtil.ItemCallback<Score>() {
                override fun areItemsTheSame(old: Score, new: Score) = old.id == new.id
                override fun areContentsTheSame(old: Score, new: Score) = old == new
            }
        }
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

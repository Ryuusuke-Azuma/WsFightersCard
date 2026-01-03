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
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.databinding.FragmentScheduleBinding
import com.mynet.kazekima.wsfighterscard.schedule.record.RecordScoreDialogFragment
import com.mynet.kazekima.wsfighterscard.schedule.record.RecordDialogFragment
import java.time.LocalDate
import java.time.ZoneId

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

        // アダプターの初期化 (タップ時にスコア入力ダイアログを表示)
        val adapter = ScheduleListAdapter { game ->
            val scoreDialog = RecordScoreDialogFragment.newInstance(game.id, game.game_name ?: "")
            scoreDialog.show(childFragmentManager, "score")
        }
        binding.recyclerView.adapter = adapter

        // カレンダーの日付変更を監視
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            viewModel.setSelectedDate(LocalDate.of(year, month + 1, dayOfMonth))
        }

        // スケジュール登録完了通知
        setFragmentResultListener(RecordDialogFragment.REQUEST_KEY) { _, bundle ->
            if (bundle.getBoolean(RecordDialogFragment.RESULT_SAVED)) {
                viewModel.loadData()
            }
        }

        // スコア登録完了通知 (RecordScoreDialogFragment からの戻り)
        setFragmentResultListener(RecordScoreDialogFragment.REQUEST_KEY) { _, bundle ->
            if (bundle.getBoolean(RecordScoreDialogFragment.RESULT_SAVED)) {
                viewModel.loadData() // スコア更新を反映
            }
        }

        viewModel.games.observe(viewLifecycleOwner) { games ->
            adapter.submitList(games)
        }
        
        viewModel.loadData()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.schedule_menu, menu)
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
        val today = LocalDate.now()
        val millis = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        binding.calendarView.date = millis
        viewModel.setSelectedDate(today)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

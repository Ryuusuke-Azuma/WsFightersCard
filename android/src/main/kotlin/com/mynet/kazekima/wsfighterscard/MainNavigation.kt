/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard

import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.navigation.NavigationView
import com.mynet.kazekima.wsfighterscard.analytics.AnalyticsFragment
import com.mynet.kazekima.wsfighterscard.databinding.ActivityMainBinding
import com.mynet.kazekima.wsfighterscard.profile.ProfileFragment
import com.mynet.kazekima.wsfighterscard.schedule.RecordDialogFragment
import com.mynet.kazekima.wsfighterscard.schedule.ScheduleFragment

/**
 * FightersCard のナビゲーション（ドロワー、ツールバー、FAB）を管理するクラス
 */
class MainNavigation(private val activity: AppCompatActivity) :
    NavigationView.OnNavigationItemSelectedListener,
    DefaultLifecycleObserver {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(owner: LifecycleOwner) {
        // Activityのレイアウトにバインド
        binding = ActivityMainBinding.bind(activity.findViewById(R.id.drawer_layout))

        setupToolbar()
        setupDrawer()
        setupFab()

        // 初期表示設定
        if (owner is AppCompatActivity) {
            activity.title = "スケジュール"
            binding.navView.setCheckedItem(R.id.nav_schedule)
            replaceFragment(ScheduleFragment())
        }
    }

    private fun setupToolbar() {
        activity.setSupportActionBar(binding.toolbar)
    }

    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(
            activity,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            val newFragment = RecordDialogFragment()
            newFragment.show(activity.supportFragmentManager, "record")
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        // タイトルをクリックされた項目に変更
        activity.title = menuItem.title

        when (menuItem.itemId) {
            R.id.nav_schedule -> {
                replaceFragment(ScheduleFragment())
            }
            R.id.nav_stats -> {
                replaceFragment(AnalyticsFragment())
            }
            R.id.nav_profile -> {
                replaceFragment(ProfileFragment())
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}

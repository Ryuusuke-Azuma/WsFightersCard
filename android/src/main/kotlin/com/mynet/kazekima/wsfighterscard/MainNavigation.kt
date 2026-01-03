/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard

import android.view.MenuItem
import android.widget.Toast
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
import com.mynet.kazekima.wsfighterscard.schedule.ScheduleFragment
import com.mynet.kazekima.wsfighterscard.schedule.record.RecordGameDialogFragment

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

        // 初期表示設定 (スケジュール)
        activity.title = activity.getString(R.string.menu_schedule)
        binding.navView.setCheckedItem(R.id.nav_schedule)
        replaceFragment(ScheduleFragment())
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

    /**
     * Fragmentに応じて FAB の見た目と挙動を更新する
     */
    private fun updateFab(fragment: Fragment) {
        when (fragment) {
            is ScheduleFragment -> {
                binding.fab.show()
                binding.fab.setImageResource(R.drawable.ic_add)
                binding.fab.setOnClickListener {
                    val newFragment = RecordGameDialogFragment()
                    newFragment.show(activity.supportFragmentManager, "game")
                }
            }
            is ProfileFragment -> {
                binding.fab.show()
                binding.fab.setImageResource(R.drawable.ic_save)
                binding.fab.setOnClickListener {
                    Toast.makeText(activity, "プロフィールを保存しました", Toast.LENGTH_SHORT).show()
                }
            }
            is AnalyticsFragment -> {
                binding.fab.hide()
            }
            else -> {
                binding.fab.hide()
            }
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_schedule -> {
                activity.title = activity.getString(R.string.menu_schedule)
                replaceFragment(ScheduleFragment())
            }
            R.id.nav_analytics -> {
                activity.title = activity.getString(R.string.menu_analytics)
                replaceFragment(AnalyticsFragment())
            }
            R.id.nav_profile -> {
                activity.title = activity.getString(R.string.menu_profile)
                replaceFragment(ProfileFragment())
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        // 現在表示されている Fragment と同じクラスなら何もしない (最適化)
        val currentFragment = activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (currentFragment != null && currentFragment::class == fragment::class) {
            return
        }

        // FAB の状態を更新
        updateFab(fragment)

        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}

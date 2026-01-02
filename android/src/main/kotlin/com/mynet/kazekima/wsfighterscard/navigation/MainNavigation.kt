/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.navigation

import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.navigation.NavigationView
import com.mynet.kazekima.wsfighterscard.R
import com.mynet.kazekima.wsfighterscard.RecordDialogFragment
import com.mynet.kazekima.wsfighterscard.databinding.ActivityMainBinding

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
            // スケジュールをデフォルトのタイトルに設定
            activity.title = "スケジュール"
            // メニューの「スケジュール」を選択状態にする
            binding.navView.setCheckedItem(R.id.nav_schedule)
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
                // スケジュール画面（現在の MainActivity の内容）
            }
            R.id.nav_matches -> {
                // 対戦記録画面
            }
            R.id.nav_stats -> {
                // 成績グラフ画面
            }
            R.id.nav_profile -> {
                // マイプロフィール画面
            }
            R.id.nav_qr_exchange -> {
                // QRプロフィール交換画面
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}

/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.navigation.NavigationView
import com.mynet.kazekima.wsfighterscard.analytics.AnalyticsFragment
import com.mynet.kazekima.wsfighterscard.databinding.ActivityMainBinding
import com.mynet.kazekima.wsfighterscard.profile.ProfileFragment
import com.mynet.kazekima.wsfighterscard.schedule.ScheduleFragment
import com.mynet.kazekima.wsfighterscard.settings.SettingsFragment

class MainNavigation(private val activity: AppCompatActivity, private val binding: ActivityMainBinding) :
    NavigationView.OnNavigationItemSelectedListener,
    DefaultLifecycleObserver {

    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(owner: LifecycleOwner) {
        setupToolbar()
        setupDrawer()
        setupMenu(owner)
        setupBackStackListener()

        if (isNavHostEmpty()) {
            navigateTo(ScheduleFragment(), addToBackStack = false)
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val fragment = when (menuItem.itemId) {
            R.id.nav_schedule -> ScheduleFragment()
            R.id.nav_analytics -> AnalyticsFragment()
            R.id.nav_profile -> ProfileFragment()
            else -> null
        }
        fragment?.let { navigateTo(it) }
        binding.drawerMain.closeDrawer(GravityCompat.START)
        return true
    }

    fun navigateTo(fragment: Fragment, addToBackStack: Boolean = true) {
        val fragmentManager = activity.supportFragmentManager
        val currentFragment = fragmentManager.findFragmentById(R.id.nav_host_main)
        if (currentFragment != null && currentFragment::class == fragment::class) {
            return
        }

        fragmentManager.beginTransaction()
            .replace(R.id.nav_host_main, fragment)
            .apply { if (addToBackStack) addToBackStack(null) }
            .commit()
    }

    fun syncToggleState() {
        toggle.syncState()
    }

    private fun isNavHostEmpty() =
        activity.supportFragmentManager.findFragmentById(R.id.nav_host_main) == null

    private fun setupBackStackListener() {
        activity.supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = activity.supportFragmentManager.findFragmentById(R.id.nav_host_main)
            if (currentFragment != null) {
                updateTitle(currentFragment)
            }
        }
    }

    private fun updateTitle(fragment: Fragment) {
        activity.title = when (fragment) {
            is ScheduleFragment -> activity.getString(R.string.menu_schedule)
            is AnalyticsFragment -> activity.getString(R.string.menu_analytics)
            is ProfileFragment -> activity.getString(R.string.menu_profile)
            is SettingsFragment -> activity.getString(R.string.action_settings)
            else -> activity.title
        }
    }

    private fun setupToolbar() = activity.setSupportActionBar(binding.toolbarMain)

    private fun setupDrawer() {
        toggle = ActionBarDrawerToggle(
            activity, binding.drawerMain, binding.toolbarMain,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerMain.addDrawerListener(toggle)
        binding.navigationMain.setNavigationItemSelectedListener(this)
    }

    private fun forceSyncDrawerToggle() {
        // isDrawerIndicatorEnabledプロパティを一度OFF/ONすることで、DrawerToggleとToolbarの
        // 描画状態を強制的に再同期させ、画面遷移時のちらつきを防ぐ。
        toggle.isDrawerIndicatorEnabled = false
        toggle.isDrawerIndicatorEnabled = true
    }

    private fun setupMenu(owner: LifecycleOwner) {
        activity.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
                forceSyncDrawerToggle()
            }

            override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
                R.id.action_settings -> {
                    navigateTo(SettingsFragment())
                    true
                }
                else -> false
            }
        }, owner, Lifecycle.State.RESUMED)
    }
}

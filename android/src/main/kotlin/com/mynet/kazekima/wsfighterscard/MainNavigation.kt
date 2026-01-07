/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.navigation.NavigationView
import com.mynet.kazekima.wsfighterscard.analytics.AnalyticsFragment
import com.mynet.kazekima.wsfighterscard.databinding.ActivityMainBinding
import com.mynet.kazekima.wsfighterscard.profile.ProfileFragment
import com.mynet.kazekima.wsfighterscard.schedule.ScheduleFragment
import com.mynet.kazekima.wsfighterscard.schedule.record.RecordGameDialogFragment
import com.mynet.kazekima.wsfighterscard.settings.SettingsFragment

class MainNavigation(private val activity: AppCompatActivity) :
    NavigationView.OnNavigationItemSelectedListener,
    DefaultLifecycleObserver {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(owner: LifecycleOwner) {
        binding = ActivityMainBinding.bind(activity.findViewById(R.id.drawer_layout))

        setupToolbar()
        setupDrawer()
        setupMenu(owner)
        setupFragmentCallbacks()

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
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun navigateTo(fragment: Fragment, addToBackStack: Boolean = true) {
        val fragmentManager = activity.supportFragmentManager
        if (fragmentManager.findFragmentById(R.id.nav_host_fragment)?.javaClass == fragment.javaClass) return

        updateUi(fragment)
        fragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .apply { if (addToBackStack) addToBackStack(null) }
            .commit()
    }

    private fun isNavHostEmpty() =
        activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) == null

    private fun setupFragmentCallbacks() {
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    if (isMainFragment(f)) {
                        updateUi(f)
                    }
                }
            }, false
        )
    }

    private fun updateUi(fragment: Fragment) {
        updateTitle(fragment)
        updateFab(fragment)
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

    private fun updateFab(fragment: Fragment) = with(binding.fab) {
        when (fragment) {
            is ScheduleFragment -> {
                show()
                setImageResource(R.drawable.ic_add)
                contentDescription = activity.getString(R.string.dialog_record_game)
                setOnClickListener { RecordGameDialogFragment.newInstance(java.time.LocalDate.now()).show(activity.supportFragmentManager, "game") }
            }
            is ProfileFragment -> {
                show()
                setImageResource(R.drawable.ic_save)
                contentDescription = activity.getString(R.string.dialog_record_ok)
                setOnClickListener { Toast.makeText(activity, activity.getString(R.string.dialog_record_ok), Toast.LENGTH_SHORT).show() }
            }
            else -> hide()
        }
    }

    private fun isMainFragment(f: Fragment): Boolean =
        f is ScheduleFragment || f is AnalyticsFragment || f is ProfileFragment || f is SettingsFragment

    private fun setupToolbar() = activity.setSupportActionBar(binding.toolbar)

    private fun setupDrawer() {
        ActionBarDrawerToggle(
            activity, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        ).apply {
            binding.drawerLayout.addDrawerListener(this)
            syncState()
        }
        binding.navView.setNavigationItemSelectedListener(this)
    }

    private fun setupMenu(owner: LifecycleOwner) {
        activity.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
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

/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard

import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.navigation.NavigationView
import com.mynet.kazekima.wsfighterscard.databinding.ActivityMainBinding
import com.mynet.kazekima.wsfighterscard.record.RecordDialogFragment

class MainNavigation(private val activity: AppCompatActivity) :
    NavigationView.OnNavigationItemSelectedListener,
    DefaultLifecycleObserver {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(owner: LifecycleOwner) {
        binding = ActivityMainBinding.bind(activity.findViewById(R.id.drawer_layout))

        setupToolbar()
        setupDrawer()
        setupFab()
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
        when (menuItem.itemId) {
            R.id.nav_camera -> { /* 今後の実装 */ }
            R.id.nav_gallery -> { }
            R.id.nav_slideshow -> { }
            R.id.nav_manage -> { }
            R.id.nav_share -> { }
            R.id.nav_send -> { }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
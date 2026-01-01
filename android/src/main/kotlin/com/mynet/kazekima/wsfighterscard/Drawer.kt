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

/**
 * Drawer
 */
class Drawer(private val mActivity: AppCompatActivity) : NavigationView.OnNavigationItemSelectedListener,
    DefaultLifecycleObserver {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(owner: LifecycleOwner) {
        // Bind to the existing layout of the activity
        binding = ActivityMainBinding.bind(mActivity.findViewById(R.id.drawer_layout))

        mActivity.setSupportActionBar(binding.appBar.toolbar)

        val toggle = ActionBarDrawerToggle(
            mActivity,
            binding.drawerLayout,
            binding.appBar.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (menuItem.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {
            }
            R.id.nav_slideshow -> {
            }
            R.id.nav_manage -> {
            }
            R.id.nav_share -> {
            }
            R.id.nav_send -> {
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}

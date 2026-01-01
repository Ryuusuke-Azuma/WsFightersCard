/*
 * Copyright (c) 2025 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.MenuProvider
import com.mynet.kazekima.wsfighterscard.databinding.ActivityMainBinding
import com.mynet.kazekima.wsfighterscard.MainNavigation

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Adapter
        val adapter = MainListAdapter(this)
        binding.listView.adapter = adapter

        // Observe Data from ViewModel
        viewModel.games.observe(this) { games ->
            adapter.updateData(games)
        }
        viewModel.loadData()

        // Modern Back Press Handling
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        })

        // Modern Menu Handling using MenuProvider
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_settings -> true
                    else -> false
                }
            }
        })

        // Setup All Navigation (Drawer, Toolbar, FAB) in one observer
        lifecycle.addObserver(MainNavigation(this))
    }
}

/*
 * Copyright (c) 2025 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mynet.kazekima.wsfighterscard.databinding.ActivityMainBinding
import com.mynet.kazekima.wsfighterscard.record.RecordDialogFragment

/**
 * Pocket
 */
class Pocket(private val mActivity: AppCompatActivity) : DefaultLifecycleObserver {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(owner: LifecycleOwner) {
        // Bind to the existing layout of the activity
        binding = ActivityMainBinding.bind(mActivity.findViewById(R.id.drawer_layout))

        binding.appBar.fab.setOnClickListener {
            val newFragment = RecordDialogFragment()
            newFragment.show(mActivity.supportFragmentManager, "record")
        }
    }
}

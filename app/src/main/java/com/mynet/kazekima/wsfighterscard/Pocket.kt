/*
 * Copyright (c) 2025 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mynet.kazekima.wsfighterscard.record.RecordDialogFragment

/**
 * Pocket
 */
class Pocket(private val mActivity: AppCompatActivity) : DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        val fab = mActivity.findViewById<FloatingActionButton>(R.id.fab)
        fab?.setOnClickListener {
            val newFragment = RecordDialogFragment()
            newFragment.show(mActivity.supportFragmentManager, "record")
        }
    }
}

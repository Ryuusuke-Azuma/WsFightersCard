/*
 * Copyright (c) 2018 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mynet.kazekima.wsfighterscard.record.RecordDialogFragment;

/**
 * Pocket
 */
public class Pocket implements DefaultLifecycleObserver {

    private final AppCompatActivity mActivity;

    public Pocket(AppCompatActivity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        FloatingActionButton fab = mActivity.findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment newFragment = new RecordDialogFragment();
                    newFragment.show(mActivity.getSupportFragmentManager(), "record");
                }
            });
        }
    }
}

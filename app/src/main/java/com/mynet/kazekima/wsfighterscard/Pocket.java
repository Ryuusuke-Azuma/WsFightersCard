/*
 * Copyright (c) 2018 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mynet.kazekima.fuse.ActivityBridge;
import com.mynet.kazekima.fuse.ActivityLifecycleAdapter;
import com.mynet.kazekima.fuse.ActivityLifecycleHandler;
import com.mynet.kazekima.fuse.ActivityLifecycleListener;
import com.mynet.kazekima.wsfighterscard.record.RecordDialogFragment;

/**
 * Pocket
 */
public class Pocket extends ActivityLifecycleAdapter implements ActivityLifecycleHandler.Observer {

    @Override
    public ActivityLifecycleListener getActivityLifecycleListener() {
        return this;
    }

    @Override
    public void onActivityCreate(AppCompatActivity activity) {
        FloatingActionButton fab = activity.findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppCompatActivity activity = ActivityBridge.getInstances().getActivity();
                    if (activity != null) {
                        DialogFragment newFragment = new RecordDialogFragment();
                        newFragment.show(activity.getSupportFragmentManager(), "record");
                    }
                }
            });
        }
    }
}

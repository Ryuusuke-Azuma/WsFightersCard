/*
 * Copyright (c) 2018 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mynet.kazekima.fuse.ActivityBridge;
import com.mynet.kazekima.fuse.ActivityLifecycleAdapter;
import com.mynet.kazekima.fuse.ActivityLifecycleHandler;
import com.mynet.kazekima.fuse.ActivityLifecycleListener;
import com.mynet.kazekima.wsfighterscard.record.RecordDialogFragment;

class Pocket implements ActivityLifecycleHandler.Observer {

    final ActivityLifecycleAdapter mActivityAdapter = new ActivityLifecycleAdapter() {

        @Override
        public void onActivityCreate(AppCompatActivity activity) {
            FloatingActionButton fab = activity.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppCompatActivity activity = ActivityBridge.getInstances().getActivity();
                    DialogFragment newFragment = new RecordDialogFragment();
                    newFragment.show(activity.getSupportFragmentManager(), "record");
//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                }
            });
        }
    };

    @Override
    public ActivityLifecycleListener getActivityLifecycleListener() {
        return mActivityAdapter;
    }
}

/*
 * Copyright (c) 2018 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.mynet.kazekima.fuse.ActivityBridge;
import com.mynet.kazekima.fuse.ActivityLifecycleAdapter;
import com.mynet.kazekima.fuse.ActivityLifecycleHandler;
import com.mynet.kazekima.fuse.ActivityLifecycleListener;

/**
 * Drawer
 */
public class Drawer extends ActivityLifecycleAdapter implements
        NavigationView.OnNavigationItemSelectedListener, ActivityLifecycleHandler.Observer {

    @Override
    public ActivityLifecycleListener getActivityLifecycleListener() {
        return this;
    }

    @Override
    public void onActivityCreate(AppCompatActivity activity) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);

        DrawerLayout drawer = activity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        AppCompatActivity activity = ActivityBridge.getInstances().getActivity();
        if (activity != null) {
            DrawerLayout drawer = activity.findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}

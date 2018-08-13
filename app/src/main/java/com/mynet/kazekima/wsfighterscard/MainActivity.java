/*
 * Copyright (c) 2018 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.mynet.kazekima.fuse.ActivityBridge;
import com.mynet.kazekima.fuse.ActivityLifecycleHandler;
import com.mynet.kazekima.fuse.ActivitySession;
import com.mynet.kazekima.wsfighterscard.data.FightersDb;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    final ActivityLifecycleHandler mActivityLifecycleHandler = new ActivityLifecycleHandler();
    final ActivitySession mActivitySession = new ActivitySession() {

        @Override
        public String getTag() {
            return ActivityTag.Main.name();
        }

        @Override
        public AppCompatActivity getActivity() {
            return MainActivity.this;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityBridge.getInstances().addSession(mActivitySession);

        Set<ActivityLifecycleHandler.Observer> observers = new HashSet<>();
        Drawer drawer = new Drawer();
        Pocket pocket = new Pocket();
        observers.add(drawer);
        observers.add(pocket);

        mActivityLifecycleHandler.resister(
                observers.toArray(new ActivityLifecycleHandler.Observer[0]));
        mActivityLifecycleHandler.onActivityCreate(this);




        //テスト
        // テーブルにデータ投入.
        ContentValues values = new ContentValues();
        for (int i = 0; i < 3; i++) {
            values.clear();
            values.put(FightersDb.Game.GAME_NAME, "name" + i);
            getContentResolver().insert(FightersDb.Game.CONTENT_URI, values);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivityLifecycleHandler.onActivityDestroy(this);
        ActivityBridge.getInstances().deleteSession(mActivitySession);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

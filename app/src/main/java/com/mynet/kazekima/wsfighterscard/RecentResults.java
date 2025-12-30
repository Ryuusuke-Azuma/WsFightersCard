/*
 * Copyright (c) 2018 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.mynet.kazekima.fuse.ActivityBridge;
import com.mynet.kazekima.wsfighterscard.data.FightersDb;

public class RecentResults implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecentResultsListAdapter mAdapter;

    RecentResults() {
        AppCompatActivity activity = ActivityBridge.getInstances().getActivity();
        ListView listView = (ListView) activity.findViewById(R.id.listView);
        mAdapter = new RecentResultsListAdapter(activity, null, true);
        listView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        LoaderManager.getInstance(activity).initLoader(0, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        AppCompatActivity activity = ActivityBridge.getInstances().getActivity();
        return new CursorLoader(activity, FightersDb.Game.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void finish() {
        AppCompatActivity activity = ActivityBridge.getInstances().getActivity();
        if (activity != null) {
            LoaderManager.getInstance(activity).destroyLoader(0);
        }
    }
}

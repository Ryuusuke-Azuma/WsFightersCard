/*
 * Copyright (c) 2018 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.mynet.kazekima.fuse.ActivityBridge;
import com.mynet.kazekima.wsfighterscard.data.FightersDb;

public class RecentResults implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecentResultsListAdapter mAdapter;

    RecentResults() {
        Activity activity = ActivityBridge.getInstances().getActivity();
        ListView listView = (ListView) activity.findViewById(R.id.listView);
        mAdapter = new RecentResultsListAdapter(activity, null, true);
        listView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        activity.getLoaderManager().initLoader(0, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        Activity activity = ActivityBridge.getInstances().getActivity();
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
        Activity activity = ActivityBridge.getInstances().getActivity();
        activity.getLoaderManager().destroyLoader(0);
    }
}

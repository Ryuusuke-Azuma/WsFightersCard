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

import com.mynet.kazekima.wsfighterscard.data.FightersDb;

public class RecentResults implements LoaderManager.LoaderCallbacks<Cursor> {

    private final AppCompatActivity mActivity;
    private RecentResultsListAdapter mAdapter;

    RecentResults(AppCompatActivity activity) {
        this.mActivity = activity;
        ListView listView = (ListView) mActivity.findViewById(R.id.listView);
        mAdapter = new RecentResultsListAdapter(mActivity, null, true);
        listView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        LoaderManager.getInstance(mActivity).initLoader(0, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new CursorLoader(mActivity, FightersDb.Game.CONTENT_URI,
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
        if (mActivity != null) {
            LoaderManager.getInstance(mActivity).destroyLoader(0);
        }
    }
}

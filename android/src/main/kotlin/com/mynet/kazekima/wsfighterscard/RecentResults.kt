/*
 * Copyright (c) 2025 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard

import android.database.Cursor
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.mynet.kazekima.wsfighterscard.data.FightersDb

class RecentResults(private val mActivity: AppCompatActivity) : LoaderManager.LoaderCallbacks<Cursor> {
    private val mAdapter: RecentResultsListAdapter

    init {
        val listView = mActivity.findViewById<ListView>(R.id.listView)
        mAdapter = RecentResultsListAdapter(mActivity, null, true)
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
        LoaderManager.getInstance(mActivity).initLoader(0, null, this)
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
        return CursorLoader(mActivity, FightersDb.Game.CONTENT_URI,
                null, null, null, null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        mAdapter.swapCursor(cursor)
        mAdapter.notifyDataSetChanged()
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mAdapter.swapCursor(null)
    }

    fun finish() {
        LoaderManager.getInstance(mActivity).destroyLoader(0)
    }
}

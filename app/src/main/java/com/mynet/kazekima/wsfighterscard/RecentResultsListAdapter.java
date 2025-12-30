/*
 * Copyright (c) 2018 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import com.mynet.kazekima.wsfighterscard.data.FightersDb;

/**
 * RecentResultsListAdapter
 */
public class RecentResultsListAdapter extends CursorAdapter {

    public RecentResultsListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public RecentResultsListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.recent_result_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title = (TextView) view.findViewById(R.id.item_title);
        TextView date = (TextView) view.findViewById(R.id.item_date);

        String titleStr = cursor.getString(cursor.getColumnIndexOrThrow(FightersDb.Game.COLUMN_NAME_TITLE));
        String dateStr = cursor.getString(cursor.getColumnIndexOrThrow(FightersDb.Game.COLUMN_NAME_DATE));

        title.setText(titleStr);
        date.setText(dateStr);
    }
}

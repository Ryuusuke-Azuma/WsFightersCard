/*
 * Copyright (c) 2025 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter
import com.mynet.kazekima.wsfighterscard.data.FightersDb

/**
 * RecentResultsListAdapter
 */
class RecentResultsListAdapter : CursorAdapter {
    constructor(context: Context?, c: Cursor?, autoRequery: Boolean) : super(context, c, autoRequery)
    constructor(context: Context?, c: Cursor?, flags: Int) : super(context, c, flags)

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.recent_result_item, parent, false)
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val title = view.findViewById<View>(R.id.item_title) as TextView
        val date = view.findViewById<View>(R.id.item_date) as TextView

        val titleStr = cursor.getString(cursor.getColumnIndexOrThrow(FightersDb.Game.COLUMN_NAME_TITLE))
        val dateStr = cursor.getString(cursor.getColumnIndexOrThrow(FightersDb.Game.COLUMN_NAME_DATE))

        title.text = titleStr
        date.text = dateStr
    }
}

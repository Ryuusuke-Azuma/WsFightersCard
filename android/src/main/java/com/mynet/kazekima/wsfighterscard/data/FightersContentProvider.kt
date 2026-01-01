/*
 * Copyright (c) 2025 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.data

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

/**
 * FightersContentProvider
 */
class FightersContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.mynet.kazekima.wsfighterscard.fighters"
    }

    private var mDbHelper: DbOpenHelper? = null

    override fun onCreate(): Boolean {
        mDbHelper = DbOpenHelper(context)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val db = mDbHelper?.readableDatabase ?: return null
        return db.query(FightersDb.Game.TABLE, projection, selection, selectionArgs, null, null, sortOrder)
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val db = mDbHelper?.writableDatabase ?: return null
        val rowId = db.insert(FightersDb.Game.TABLE, null, contentValues)
        return if (rowId > 0) {
            Uri.withAppendedPath(FightersDb.Game.CONTENT_URI, rowId.toString())
        } else null
    }

    override fun delete(uri: Uri, selection: String?,
                        selectionArgs: Array<String>?): Int {
        val db = mDbHelper?.writableDatabase ?: return 0
        return db.delete(FightersDb.Game.TABLE, selection, selectionArgs)
    }

    override fun update(uri: Uri, contentValues: ContentValues?,
                        selection: String?, selectionArgs: Array<String>?): Int {
        val db = mDbHelper?.writableDatabase ?: return 0
        return db.update(FightersDb.Game.TABLE, contentValues, selection, selectionArgs)
    }
}

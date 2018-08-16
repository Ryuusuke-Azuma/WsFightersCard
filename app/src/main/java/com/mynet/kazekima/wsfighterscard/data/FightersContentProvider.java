/*
 * Copyright (c) 2018 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FightersContentProvider extends ContentProvider {
    // Authority
    public static final String AUTHORITY = "com.mynet.kazekima.wsfighterscard.fighters";

    private DbOpenHelper mDbOpenHelper = null;

    private static final int GAMES = 1;
    private static final int GAME_ID = 2;

    private static UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, FightersDb.Game.TABLE, GAMES);
        sUriMatcher.addURI(AUTHORITY, FightersDb.Game.TABLE + "/#", GAME_ID);
    }

    @Override
    public boolean onCreate() {
        mDbOpenHelper = new DbOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case GAMES:
            case GAME_ID:
                queryBuilder.setTables(FightersDb.Game.TABLE);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        String insertTable;
        Uri contentUri;
        switch (sUriMatcher.match(uri)) {
            case GAMES:
                insertTable = FightersDb.Game.TABLE;
                contentUri = FightersDb.Game.CONTENT_URI;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        long rowId = db.insert(insertTable, null, contentValues);
        if (rowId > 0) {
            Uri returnUri = ContentUris.withAppendedId(contentUri, rowId);
            Context context = getContext();
            if (context == null) {
                return returnUri;
            }
            getContext().getContentResolver().notifyChange(returnUri, null);
            return returnUri;
        } else {
            throw new IllegalArgumentException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case GAMES:
            case GAME_ID:
                count = db.delete(FightersDb.Game.TABLE, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        String updateTable;
        int count;
        switch (sUriMatcher.match(uri)) {
            case GAMES:
            case GAME_ID:
                updateTable = FightersDb.Game.TABLE;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        count = db.update(updateTable, contentValues, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}

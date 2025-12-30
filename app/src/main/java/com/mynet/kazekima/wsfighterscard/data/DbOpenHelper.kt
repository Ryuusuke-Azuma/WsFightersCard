/*
 * Copyright (c) 2018 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.mynet.kazekima.wsfighterscard.data.FightersDb.DATABASE_NAME;
import static com.mynet.kazekima.wsfighterscard.data.FightersDb.DATABASE_VERSION;
import static com.mynet.kazekima.wsfighterscard.data.Sql.CREATE_TABLE;
import static com.mynet.kazekima.wsfighterscard.data.Sql.DROP_TABLE_IF_EXISTS;
import static com.mynet.kazekima.wsfighterscard.data.Sql.PRIMARY_KEY_AUTOINCREMENT;
import static com.mynet.kazekima.wsfighterscard.data.Sql._HS;
import static com.mynet.kazekima.wsfighterscard.data.Sql._LE;

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final String CREATE_TABLE_GAME = CREATE_TABLE + _HS
            + FightersDb.Game.TABLE + _HS + "("
            + FightersDb.Game._ID + _HS + "integer" + _HS + PRIMARY_KEY_AUTOINCREMENT + ", "
            + FightersDb.Game.GAME_NAME + _HS + "text" + ", "
            + FightersDb.Game.GAME_DATE + _HS + "date" + ", "
            + FightersDb.Game.BATTLE_DECK + _HS + "text" + ", "
            + FightersDb.Game.MEMO + _HS + "text"
            + ")" + _LE;

    private static final String CREATE_TABLE_SCORE = CREATE_TABLE + _HS
            + FightersDb.Score.TABLE + _HS + "("
            + FightersDb.Score._ID + _HS + "integer" + _HS + PRIMARY_KEY_AUTOINCREMENT + ", "
            + FightersDb.Score.GAME_ID + _HS + "integer" + ", "
            + FightersDb.Score.MATCHING_DECK + _HS + "text" + ", "
            + FightersDb.Score.WIN_OR_LOSE + _HS + "integer" + ", "
            + FightersDb.Score.MEMO + _HS + "text"
            + ")" + _LE;

    DbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_GAME);
        db.execSQL(CREATE_TABLE_SCORE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_IF_EXISTS + _HS + FightersDb.Game.TABLE + _LE);
        db.execSQL(DROP_TABLE_IF_EXISTS + _HS + FightersDb.Score.TABLE + _LE);
        onCreate(db);
    }
}

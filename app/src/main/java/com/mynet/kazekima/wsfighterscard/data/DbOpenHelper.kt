/*
 * Copyright (c) 2025 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbOpenHelper(context: Context?) : SQLiteOpenHelper(context, FightersDb.DATABASE_NAME, null, FightersDb.DATABASE_VERSION) {

    companion object {
        private val CREATE_TABLE_GAME = (Sql.CREATE_TABLE + Sql._HS
                + FightersDb.Game.TABLE + Sql._HS + "("
                + FightersDb.Game._ID + Sql._HS + "integer" + Sql._HS + Sql.PRIMARY_KEY_AUTOINCREMENT + ", "
                + FightersDb.Game.GAME_NAME + Sql._HS + "text" + ", "
                + FightersDb.Game.GAME_DATE + Sql._HS + "date" + ", "
                + FightersDb.Game.BATTLE_DECK + Sql._HS + "text" + ", "
                + FightersDb.Game.MEMO + Sql._HS + "text"
                + ")" + Sql._LE)

        private val CREATE_TABLE_SCORE = (Sql.CREATE_TABLE + Sql._HS
                + FightersDb.Score.TABLE + Sql._HS + "("
                + FightersDb.Score._ID + Sql._HS + "integer" + Sql._HS + Sql.PRIMARY_KEY_AUTOINCREMENT + ", "
                + FightersDb.Score.GAME_ID + Sql._HS + "integer" + ", "
                + FightersDb.Score.MATCHING_DECK + Sql._HS + "text" + ", "
                + FightersDb.Score.WIN_OR_LOSE + Sql._HS + "integer" + ", "
                + FightersDb.Score.MEMO + Sql._HS + "text"
                + ")" + Sql._LE)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_GAME)
        db.execSQL(CREATE_TABLE_SCORE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(Sql.DROP_TABLE_IF_EXISTS + Sql._HS + FightersDb.Game.TABLE + Sql._LE)
        db.execSQL(Sql.DROP_TABLE_IF_EXISTS + Sql._HS + FightersDb.Score.TABLE + Sql._LE)
        onCreate(db)
    }
}

/*
 * Copyright (c) 2018 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.data;

import android.content.ContentValues;
import android.database.Cursor;

public class GameEntity {

    private int mGameId;
    private String mGameName;
    private String mGameDate;
    private String mBattleDeck;
    private String mMemo;

    public enum GameBean {
        _id("integer primary key autoincrement"),
        game_name("text"),
        game_date("date"),
        battle_deck("text"),
        memo("text");

        private String mType;
        private String mWhere;

        GameBean(String type) {
            mType = type;
            mWhere = name() + "=?";
        }
        public String type() {return mType;}
        public String where() {return mWhere;}
        public int getInt(Cursor cursor) {return cursor.getInt(cursor.getColumnIndexOrThrow(name()));}
        public String getString(Cursor cursor) {return cursor.getString(cursor.getColumnIndexOrThrow(name()));}
        public void put(ContentValues values, int val) {values.put(name(), val);}
        public void put(ContentValues values, String val) {values.put(name(), val);}
    }

    public static GameEntity toEntity(Cursor cursor) {
        GameEntity entity = new GameEntity();
        entity.mGameId = GameBean._id.getInt(cursor);
        entity.mGameName = GameBean.game_name.getString(cursor);
        entity.mBattleDeck = GameBean.battle_deck.getString(cursor);
        entity.mMemo = GameBean.memo.getString(cursor);
        return entity;
    }

    public ContentValues fromEntity(GameEntity entity) {
        ContentValues values = new ContentValues();
        GameBean._id.put(values, entity.getGameId());
        GameBean.game_name.put(values, entity.getGameName());
        GameBean.battle_deck.put(values, entity.getBattleDeck());
        GameBean.memo.put(values, entity.getMemo());
        return values;
    }

    public int getGameId() {
        return mGameId;
    }

    public String getGameName() {
        return mGameName;
    }

    public String getGameDate() {
        return mGameDate;
    }

    public String getBattleDeck() {
        return mBattleDeck;
    }

    public String getMemo() {
        return mMemo;
    }
}

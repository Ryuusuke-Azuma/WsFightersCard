/*
 * Copyright (c) 2018 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class FightersDb {

    static final String DATABASE_NAME = "fighters.db";
    static final int DATABASE_VERSION = 1;

    public static final class Game implements BaseColumns {
        public static final String TABLE = "game";
        public static final String TABLE_NAME = TABLE; // Compatibility
        // コンテントURI
        public static final Uri CONTENT_URI
                = Uri.parse("content://" + FightersContentProvider.AUTHORITY + "/" + TABLE);

        public static final String GAME_NAME = "game_name";
        public static final String COLUMN_NAME_TITLE = GAME_NAME; // Compatibility
        public static final String GAME_DATE = "game_date";
        public static final String COLUMN_NAME_DATE = GAME_DATE; // Compatibility
        public static final String BATTLE_DECK = "battle_deck";
        public static final String MEMO = "memo";

        Game() {
            // Constant class
        }
    }

    public static final class Score implements BaseColumns {
        public static final String TABLE = "score";
        public static final String TABLE_NAME = TABLE; // Compatibility
        // コンテントURI
        public static final Uri CONTENT_URI
                = Uri.parse("content://" + FightersContentProvider.AUTHORITY + "/" + TABLE);

        public static final String GAME_ID = "game_id";
        public static final String MATCHING_DECK = "matching_deck";
        public static final String WIN_OR_LOSE = "win_or_lose";
        public static final String MEMO = "memo";

        Score() {
            // Constant class
        }
    }

    FightersDb() {
        // Constant class
    }
}

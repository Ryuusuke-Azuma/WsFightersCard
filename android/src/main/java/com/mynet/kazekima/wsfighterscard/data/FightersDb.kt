/*
 * Copyright (c) 2025 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.data

import android.net.Uri
import android.provider.BaseColumns

object FightersDb {
    const val DATABASE_NAME = "fighters.db"
    const val DATABASE_VERSION = 1

    object Game : BaseColumns {
        const val TABLE = "game"
        const val TABLE_NAME = TABLE // Compatibility
        
        val CONTENT_URI: Uri = Uri.parse("content://${FightersContentProvider.AUTHORITY}/$TABLE")

        const val GAME_NAME = "game_name"
        const val COLUMN_NAME_TITLE = GAME_NAME // Compatibility
        const val GAME_DATE = "game_date"
        const val COLUMN_NAME_DATE = GAME_DATE // Compatibility
        const val BATTLE_DECK = "battle_deck"
        const val MEMO = "memo"
        
        const val _ID = BaseColumns._ID
    }

    object Score : BaseColumns {
        const val TABLE = "score"
        const val TABLE_NAME = TABLE // Compatibility
        
        val CONTENT_URI: Uri = Uri.parse("content://${FightersContentProvider.AUTHORITY}/$TABLE")

        const val GAME_ID = "game_id"
        const val MATCHING_DECK = "matching_deck"
        const val WIN_OR_LOSE = "win_or_lose"
        const val MEMO = "memo"
        
        const val _ID = BaseColumns._ID
    }
}

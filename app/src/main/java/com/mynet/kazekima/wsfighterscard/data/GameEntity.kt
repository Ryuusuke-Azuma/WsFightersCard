/*
 * Copyright (c) 2018 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.data

import android.content.ContentValues
import android.database.Cursor

data class GameEntity(
    var gameId: Int = 0,
    var gameName: String? = null,
    var gameDate: String? = null,
    var battleDeck: String? = null,
    var memo: String? = null
) {
    enum class GameBean(val type: String) {
        _id("integer primary key autoincrement"),
        game_name("text"),
        game_date("date"),
        battle_deck("text"),
        memo("text");

        val where: String = "$name = ?"

        fun getInt(cursor: Cursor): Int = cursor.getInt(cursor.getColumnIndexOrThrow(name))
        fun getString(cursor: Cursor): String? = cursor.getString(cursor.getColumnIndexOrThrow(name))
        fun put(values: ContentValues, value: Int) = values.put(name, value)
        fun put(values: ContentValues, value: String?) = values.put(name, value)
    }

    companion object {
        @JvmStatic
        fun toEntity(cursor: Cursor): GameEntity {
            return GameEntity(
                gameId = GameBean._id.getInt(cursor),
                gameName = GameBean.game_name.getString(cursor),
                gameDate = GameBean.game_date.getString(cursor),
                battleDeck = GameBean.battle_deck.getString(cursor),
                memo = GameBean.memo.getString(cursor)
            )
        }
    }

    fun fromEntity(): ContentValues {
        val values = ContentValues()
        if (gameId != 0) GameBean._id.put(values, gameId)
        GameBean.game_name.put(values, gameName)
        GameBean.game_date.put(values, gameDate)
        GameBean.battle_deck.put(values, battleDeck)
        GameBean.memo.put(values, memo)
        return values
    }
}

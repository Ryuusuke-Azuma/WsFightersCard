/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.db

class FightersRepository(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = FightersDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.fightersDatabaseQueries

    fun getAllGames(): List<Game> {
        return dbQuery.selectAllGames().executeAsList()
    }

    fun getGameCount(): Long {
        return dbQuery.countGames().executeAsOne()
    }

    fun addGame(name: String?, date: String?, deck: String?, memo: String?) {
        dbQuery.insertGame(name, date, deck, memo)
    }

    fun deleteGame(id: Long) {
        dbQuery.deleteGame(id)
    }
}

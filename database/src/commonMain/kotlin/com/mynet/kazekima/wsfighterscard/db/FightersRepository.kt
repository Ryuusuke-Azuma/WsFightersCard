/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.db

/**
 * データベースへのアクセスを管理する共通リポジトリ
 */
class FightersRepository(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = FightersDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.fightersDatabaseQueries

    // --- Game (Schedule) Operations ---

    fun getAllGames(): List<Game> {
        return dbQuery.selectAllGames().executeAsList()
    }

    fun getGamesByDate(date: String): List<Game> {
        return dbQuery.selectGamesByDate(date).executeAsList()
    }

    fun getGameById(id: Long): Game? {
        return dbQuery.selectAllGames().executeAsList().find { it.id == id }
    }

    fun getGameCount(): Long {
        return dbQuery.countGames().executeAsOne()
    }

    fun addGame(name: String?, date: String?, memo: String?) {
        dbQuery.insertGame(name, date, memo)
    }

    fun deleteGame(id: Long) {
        dbQuery.deleteGame(id)
    }

    fun deleteAllGames() {
        dbQuery.deleteAllGames()
    }

    // --- Score (Match Results) Operations ---

    fun getScoresForGame(gameId: Long): List<Score> {
        return dbQuery.selectScoresForGame(gameId).executeAsList()
    }

    fun addScore(gameId: Long, battleDeck: String?, matchingDeck: String?, winOrLose: Long, memo: String?) {
        dbQuery.insertScore(gameId, battleDeck, matchingDeck, winOrLose, memo)
    }
}

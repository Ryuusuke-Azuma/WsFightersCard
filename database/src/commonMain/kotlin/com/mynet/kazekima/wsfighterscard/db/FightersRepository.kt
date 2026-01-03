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

    fun getGamesWithStatsByDate(date: String): List<SelectGamesWithStatsByDate> {
        return dbQuery.selectGamesWithStatsByDate(date).executeAsList()
    }

    fun getGameDates(): List<String> {
        return dbQuery.selectDistinctGameDates().executeAsList().mapNotNull { it.game_date }
    }

    fun getGameCount(): Long {
        return dbQuery.countGames().executeAsOne()
    }

    fun addGame(name: String?, date: String?, memo: String?) {
        dbQuery.insertGame(name, date, memo)
    }

    fun addGames(games: List<Triple<String, String, String>>) {
        dbQuery.transaction {
            games.forEach { (name, date, memo) ->
                dbQuery.insertGame(name, date, memo)
            }
        }
    }

    fun deleteGame(id: Long) {
        dbQuery.transaction {
            dbQuery.deleteScoresByGameId(id)
            dbQuery.deleteGame(id)
        }
    }

    fun deleteAllGames() {
        dbQuery.deleteAllScores()
        dbQuery.deleteAllGames()
    }

    fun getScoresForGame(gameId: Long): List<Score> {
        return dbQuery.selectScoresForGame(gameId).executeAsList()
    }

    fun addScore(gameId: Long, battleDeck: String?, matchingDeck: String?, winOrLose: Long, memo: String?) {
        dbQuery.insertScore(gameId, battleDeck, matchingDeck, winOrLose, memo)
    }
}

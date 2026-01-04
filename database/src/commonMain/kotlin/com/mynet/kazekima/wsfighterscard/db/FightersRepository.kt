/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.db

import app.cash.sqldelight.ColumnAdapter
import com.mynet.kazekima.wsfighterscard.db.enums.GameStyle
import com.mynet.kazekima.wsfighterscard.db.enums.TeamResult
import com.mynet.kazekima.wsfighterscard.db.enums.TeamWinLose
import com.mynet.kazekima.wsfighterscard.db.enums.WinLose

class FightersRepository(databaseDriverFactory: DatabaseDriverFactory) {

    private val gameStyleAdapter = object : ColumnAdapter<GameStyle, Long> {
        override fun decode(databaseValue: Long): GameStyle = GameStyle.fromId(databaseValue)
        override fun encode(value: GameStyle): Long = value.id
    }

    private val winLoseAdapter = object : ColumnAdapter<WinLose, Long> {
        override fun decode(databaseValue: Long): WinLose = WinLose.fromId(databaseValue)
        override fun encode(value: WinLose): Long = value.id
    }

    private val teamResultAdapter = object : ColumnAdapter<TeamResult, Long> {
        override fun decode(databaseValue: Long): TeamResult = TeamResult.fromId(databaseValue)!!
        override fun encode(value: TeamResult): Long = value.id
    }

    private val teamWinLoseAdapter = object : ColumnAdapter<TeamWinLose, Long> {
        override fun decode(databaseValue: Long): TeamWinLose = TeamWinLose.fromId(databaseValue)
        override fun encode(value: TeamWinLose): Long = value.id
    }

    private val database = FightersDatabase(
        driver = databaseDriverFactory.createDriver(),
        gameAdapter = Game.Adapter(
            game_styleAdapter = gameStyleAdapter
        ),
        scoreAdapter = Score.Adapter(
            win_loseAdapter = winLoseAdapter,
            team_resultAdapter = teamResultAdapter,
            team_win_loseAdapter = teamWinLoseAdapter
        )
    )
    private val dbQuery = database.fightersDatabaseQueries

    fun getAllGames(): List<Game> = dbQuery.selectAllGames().executeAsList()

    fun getGamesWithStatsByDate(dateMillis: Long): List<SelectGamesWithStatsByDate> {
        return dbQuery.selectGamesWithStatsByDate(dateMillis).executeAsList()
    }

    fun getGameDates(): List<Long> {
        return dbQuery.selectDistinctGameDates().executeAsList()
    }

    fun getGameCount(): Long = dbQuery.countGames().executeAsOne()

    fun addGame(name: String, dateMillis: Long, style: GameStyle, memo: String) {
        dbQuery.insertGame(name, dateMillis, style, memo)
    }

    fun lastInsertId(): Long = dbQuery.lastInsertId().executeAsOne()

    fun addGamesWithStyles(games: List<Triple<String, Long, GameStyle>>, memos: List<String>) {
        dbQuery.transaction {
            games.forEachIndexed { index, (name, date, style) ->
                dbQuery.insertGame(name, date, style, memos[index])
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

    fun addScore(
        gameId: Long, 
        battleDeck: String, 
        matchingDeck: String, 
        winLose: WinLose, 
        teamResult: TeamResult?, 
        teamWinLose: TeamWinLose?, 
        memo: String
    ) {
        dbQuery.insertScore(gameId, battleDeck, matchingDeck, winLose, teamResult, teamWinLose, memo)
    }
}

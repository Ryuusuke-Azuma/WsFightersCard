/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.db

import app.cash.sqldelight.ColumnAdapter
import com.mynet.kazekima.wsfighterscard.db.enums.GameStyle
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

    private val teamWinLoseAdapter = object : ColumnAdapter<TeamWinLose, Long> {
        override fun decode(databaseValue: Long): TeamWinLose = TeamWinLose.fromId(databaseValue)!!
        override fun encode(value: TeamWinLose): Long = value.id
    }

    private val database = FightersDatabase(
        driver = databaseDriverFactory.createDriver(),
        gameAdapter = Game.Adapter(
            game_styleAdapter = gameStyleAdapter
        ),
        scoreAdapter = Score.Adapter(
            win_loseAdapter = winLoseAdapter,
            team_win_loseAdapter = teamWinLoseAdapter
        )
    )
    private val dbQuery = database.fightersDatabaseQueries

    fun getAllGames(): List<Game> = dbQuery.selectAllGames().executeAsList()

    fun getGamesByDate(dateMillis: Long): List<Game> {
        return dbQuery.selectGamesByDate(dateMillis).executeAsList()
    }

    fun getGameDates(): List<Long> {
        return dbQuery.selectDistinctGameDates().executeAsList()
    }

    fun addGame(name: String, dateMillis: Long, style: GameStyle, memo: String) {
        dbQuery.insertGame(name, dateMillis, style, memo)
    }

    fun updateGame(id: Long, name: String, dateMillis: Long, style: GameStyle, memo: String) {
        dbQuery.updateGame(name, dateMillis, style, memo, id)
    }

    fun lastInsertId(): Long = dbQuery.lastInsertId().executeAsOne()

    fun deleteGame(id: Long) {
        dbQuery.deleteGame(id)
    }

    fun deleteAllGames() {
        dbQuery.deleteAllGames()
    }

    fun getScoresForGame(gameId: Long): List<Score> {
        return dbQuery.selectScoresForGame(gameId).executeAsList()
    }

    fun getAllScores(): List<Score> {
        return dbQuery.selectAllScores().executeAsList()
    }

    fun addScore(
        gameId: Long, 
        battleDeck: String, 
        matchingDeck: String, 
        winLose: WinLose, 
        teamWinLose: TeamWinLose?, 
        memo: String
    ) {
        dbQuery.insertScore(gameId, battleDeck, matchingDeck, winLose, teamWinLose, memo)
    }

    fun updateScore(
        id: Long,
        battleDeck: String,
        matchingDeck: String,
        winLose: WinLose,
        teamWinLose: TeamWinLose?,
        memo: String
    ) {
        dbQuery.updateScore(battleDeck, matchingDeck, winLose, teamWinLose, memo, id)
    }

    fun deleteScore(id: Long) {
        dbQuery.deleteScore(id)
    }

    fun getAllFighters(): List<Fighter> = dbQuery.selectAllFighters().executeAsList()

    fun addFighter(name: String, isSelf: Long, memo: String) {
        dbQuery.insertFighter(name, isSelf, memo)
    }

    fun updateFighter(id: Long, name: String, isSelf: Long, memo: String) {
        dbQuery.updateFighter(name, isSelf, memo, id)
    }

    fun deleteFighter(id: Long) {
        dbQuery.deleteFighter(id)
    }

    fun getDecksByFighterId(fighterId: Long): List<Deck> = dbQuery.selectDecksByFighterId(fighterId).executeAsList()

    fun addDeck(fighterId: Long, deckName: String, memo: String) {
        dbQuery.insertDeck(fighterId, deckName, memo)
    }

    fun updateDeck(id: Long, deckName: String, memo: String) {
        dbQuery.updateDeck(deckName, memo, id)
    }

    fun deleteDeck(id: Long) {
        dbQuery.deleteDeck(id)
    }
}

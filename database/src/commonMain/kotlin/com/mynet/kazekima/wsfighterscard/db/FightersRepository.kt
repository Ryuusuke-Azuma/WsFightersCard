/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.db

import app.cash.sqldelight.ColumnAdapter
import com.mynet.kazekima.wsfighterscard.db.enums.FirstSecond
import com.mynet.kazekima.wsfighterscard.db.enums.GameStyle
import com.mynet.kazekima.wsfighterscard.db.enums.TeamWinLose
import com.mynet.kazekima.wsfighterscard.db.enums.WinLose

class FightersRepository(databaseDriverFactory: DatabaseDriverFactory) {

    private val gameStyleAdapter = object : ColumnAdapter<GameStyle, Long> {
        override fun decode(databaseValue: Long): GameStyle = GameStyle.fromId(databaseValue)
        override fun encode(value: GameStyle): Long = value.id
    }

    private val firstSecondAdapter = object : ColumnAdapter<FirstSecond, Long> {
        override fun decode(databaseValue: Long): FirstSecond = FirstSecond.fromId(databaseValue)
        override fun encode(value: FirstSecond): Long = value.id
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
            first_secondAdapter = firstSecondAdapter,
            win_loseAdapter = winLoseAdapter,
            team_win_loseAdapter = teamWinLoseAdapter
        )
    )
    private val dbQuery = database.fightersDatabaseQueries

    fun lastInsertId(): Long = dbQuery.lastInsertId().executeAsOne()

    fun getAllGames(): List<Game> = dbQuery.selectAllGames().executeAsList()

    fun getGameDates(): List<Long> {
        return dbQuery.selectDistinctGameDates().executeAsList()
    }

    fun getGamesByDate(dateMillis: Long): List<Game> {
        return dbQuery.selectGamesByDate(dateMillis).executeAsList()
    }

    fun addGame(name: String, dateMillis: Long, style: GameStyle, memo: String): Long = dbQuery.transactionWithResult {
        dbQuery.insertGame(name, dateMillis, style, memo)
        dbQuery.lastInsertId().executeAsOne()
    }

    fun updateGame(id: Long, name: String, dateMillis: Long, style: GameStyle, memo: String) {
        dbQuery.updateGame(name, dateMillis, style, memo, id)
    }

    fun deleteGame(id: Long) {
        dbQuery.deleteGame(id)
    }

    fun getAllScores(): List<Score> {
        return dbQuery.selectAllScores().executeAsList()
    }

    fun getScoresForGame(gameId: Long): List<Score> {
        return dbQuery.selectScoresForGame(gameId).executeAsList()
    }

    fun addScore(
        gameId: Long,
        battleDeck: String,
        matchingDeck: String,
        firstSecond: FirstSecond,
        winLose: WinLose,
        teamWinLose: TeamWinLose?,
        memo: String
    ): Long = dbQuery.transactionWithResult {
        dbQuery.insertScore(gameId, battleDeck, matchingDeck, firstSecond, winLose, teamWinLose, memo)
        dbQuery.lastInsertId().executeAsOne()
    }

    fun updateScore(
        id: Long,
        battleDeck: String,
        matchingDeck: String,
        firstSecond: FirstSecond,
        winLose: WinLose,
        teamWinLose: TeamWinLose?,
        memo: String
    ) {
        dbQuery.updateScore(battleDeck, matchingDeck, firstSecond, winLose, teamWinLose, memo, id)
    }

    fun deleteScore(id: Long) {
        dbQuery.deleteScore(id)
    }

    fun getAllFighters(): List<Fighter> = dbQuery.selectAllFighters().executeAsList()

    fun getSelfFighterId(): Long? = dbQuery.selectSelfFighterId().executeAsOneOrNull()

    fun addFighter(name: String, isSelf: Long, memo: String): Long = dbQuery.transactionWithResult {
        dbQuery.insertFighter(name, isSelf, memo)
        dbQuery.lastInsertId().executeAsOne()
    }

    fun updateFighter(id: Long, name: String, isSelf: Long, memo: String) {
        dbQuery.updateFighter(name, isSelf, memo, id)
    }

    fun deleteFighter(id: Long) {
        dbQuery.deleteFighter(id)
    }

    fun setSelfFighter(id: Long) {
        dbQuery.transaction {
            dbQuery.resetAllSelfFighters()
            dbQuery.setSelfFighter(id)
        }
    }

    fun getDecksByFighterId(fighterId: Long): List<Deck> = dbQuery.selectDecksByFighterId(fighterId).executeAsList()

    fun addDeck(fighterId: Long, deckName: String, memo: String): Long = dbQuery.transactionWithResult {
        dbQuery.insertDeck(fighterId, deckName, memo)
        dbQuery.lastInsertId().executeAsOne()
    }

    fun updateDeck(id: Long, deckName: String, memo: String) {
        dbQuery.updateDeck(deckName, memo, id)
    }

    fun deleteDeck(id: Long) {
        dbQuery.deleteDeck(id)
    }
}

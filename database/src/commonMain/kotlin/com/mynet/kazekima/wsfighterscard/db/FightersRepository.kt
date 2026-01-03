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

    /**
     * 指定された日付のスケジュールを、戦績集計付きで取得する
     */
    fun getGamesWithStatsByDate(date: String): List<SelectGamesWithStatsByDate> {
        return dbQuery.selectGamesWithStatsByDate(date).executeAsList()
    }

    fun getGameCount(): Long {
        return dbQuery.countGames().executeAsOne()
    }

    fun addGame(name: String?, date: String?, memo: String?) {
        dbQuery.insertGame(name, date, memo)
    }

    /**
     * 複数のスケジュールをトランザクション内で一括登録する
     */
    fun addGames(games: List<Triple<String, String, String>>) {
        dbQuery.transaction {
            games.forEach { (name, date, memo) ->
                dbQuery.insertGame(name, date, memo)
            }
        }
    }

    fun deleteGame(id: Long) {
        dbQuery.deleteGame(id)
    }

    /**
     * すべてのスケジュールと対戦結果を削除する
     */
    fun deleteAllGames() {
        dbQuery.deleteAllScores()
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

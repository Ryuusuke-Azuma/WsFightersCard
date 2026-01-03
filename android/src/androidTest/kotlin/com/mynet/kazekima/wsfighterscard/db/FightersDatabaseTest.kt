/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.db

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class FightersDatabaseTest {

    @Test
    fun testDeleteAllGamesAndScores() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val repository = FightersRepository(DatabaseDriverFactory(appContext))

        // 1. データ投入 (Game + Score)
        repository.addGame("Temp Event", "2026/01/01", "To be deleted")
        val gameId = repository.getGamesWithStatsByDate("2026/01/01")[0].id
        repository.addScore(gameId, "MyDeck", "Opponent", 1, "Score to be deleted")
        
        assertTrue(repository.getGameCount() > 0)
        assertTrue(repository.getScoresForGame(gameId).isNotEmpty())

        // 2. 全削除を実行 (Repository内で Score -> Game の順で消される)
        repository.deleteAllGames()

        // 3. どちらも空になったことを検証
        assertEquals("Games should be empty", 0L, repository.getGameCount())
        assertEquals("Scores should be empty for the gameId", 0, repository.getScoresForGame(gameId).size)
    }

    @Test
    fun seedDebugData() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val repository = FightersRepository(DatabaseDriverFactory(appContext))
        
        val countBefore = repository.getGameCount()
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        
        val dummyEvents = listOf(
            "ショップ大会 (秋葉原)",
            "フリー対戦会",
            "WGP 地区予選",
            "交流会",
            "友人との練習会"
        )

        dummyEvents.forEach { name ->
            repository.addGame(
                name = name,
                date = today,
                memo = "Test data inserted by unit test."
            )
        }

        val countAfter = repository.getGameCount()
        assertTrue("Game count should increase after seeding", countAfter > countBefore)
    }

    @Test
    fun testGameStatsAggregation() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val repository = FightersRepository(DatabaseDriverFactory(appContext))
        
        // 1. 全削除してクリーンな状態にする
        repository.deleteAllGames()
        
        val testDate = "2026/01/01"
        val eventName = "Aggregation Test Event"
        
        // 2. イベントを追加
        repository.addGame(eventName, testDate, "Aggregation test")
        val games = repository.getGamesWithStatsByDate(testDate)
        assertEquals(1, games.size)
        val gameId = games[0].id

        // 3. スコアを投入 (2勝 1敗)
        repository.addScore(gameId, "MyDeck", "Opponent1", 1, "Win 1")
        repository.addScore(gameId, "MyDeck", "Opponent2", 1, "Win 2")
        repository.addScore(gameId, "MyDeck", "Opponent3", 0, "Loss 1")

        // 4. 集計結果を検証
        val stats = repository.getGamesWithStatsByDate(testDate)
        assertEquals(1, stats.size)
        val result = stats[0]
        
        assertEquals("Win count should be 2", 2L, result.win_count)
        assertEquals("Loss count should be 1", 1L, result.loss_count)
        assertEquals("Event name should match", eventName, result.game_name)
    }
}

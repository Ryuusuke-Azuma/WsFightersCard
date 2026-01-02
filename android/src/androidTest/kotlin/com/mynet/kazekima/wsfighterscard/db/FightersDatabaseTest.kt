/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.db

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class FightersDatabaseTest {

    @Test
    fun seedDebugData() {
        // コンテキストの取得
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        
        // 実際のリポジトリを生成 (DatabaseDriverFactory を使用)
        val repository = FightersRepository(DatabaseDriverFactory(appContext))
        
        // 今日の日付を取得
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        
        // テストデータを5つ投入
        val dummyEvents = listOf(
            "ショップ大会 (秋葉原)",
            "フリー対戦会",
            "WGP 地区予選",
            "交流会",
            "友人との練習会"
        )

        dummyEvents.forEachIndexed { index, name ->
            repository.addGame(
                name = name,
                date = today,
                deck = "My Favorite Deck ${index + 1}",
                memo = "Test data inserted by unit test."
            )
        }

        // 検証: データが5つ以上存在することを確認
        val allGames = repository.getAllGames()
        assertTrue(allGames.size >= 5, "Database should have at least 5 games")
    }
}

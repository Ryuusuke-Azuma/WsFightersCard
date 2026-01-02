/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.db

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class FightersDatabaseTest {

    @Test
    fun seedDebugData() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val repository = FightersRepository(DatabaseDriverFactory(appContext))
        
        // 投入前の件数を確認
        val countBefore = repository.getGameCount()
        println("Count before seeding: $countBefore")

        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        
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

        // 投入後の件数を確認
        val countAfter = repository.getGameCount()
        println("Count after seeding: $countAfter")

        assertTrue("Game count should increase after seeding", countAfter > countBefore)
        assertTrue("Database should have at least 5 games", countAfter >= 5)
    }
}

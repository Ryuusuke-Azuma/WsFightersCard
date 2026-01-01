package com.mynet.kazekima.wsfighterscard.db.database

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.mynet.kazekima.wsfighterscard.db.FightersDatabase
import com.mynet.kazekima.wsfighterscard.db.FightersDatabaseQueries
import kotlin.Long
import kotlin.Unit
import kotlin.reflect.KClass

internal val KClass<FightersDatabase>.schema: SqlSchema<QueryResult.Value<Unit>>
  get() = FightersDatabaseImpl.Schema

internal fun KClass<FightersDatabase>.newInstance(driver: SqlDriver): FightersDatabase =
    FightersDatabaseImpl(driver)

private class FightersDatabaseImpl(
  driver: SqlDriver,
) : TransacterImpl(driver), FightersDatabase {
  override val fightersDatabaseQueries: FightersDatabaseQueries = FightersDatabaseQueries(driver)

  public object Schema : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long
      get() = 1

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
      driver.execute(null, """
          |CREATE TABLE game (
          |    id INTEGER PRIMARY KEY AUTOINCREMENT,
          |    game_name TEXT,
          |    game_date TEXT,
          |    battle_deck TEXT,
          |    memo TEXT
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE score (
          |    id INTEGER PRIMARY KEY AUTOINCREMENT,
          |    game_id INTEGER NOT NULL,
          |    matching_deck TEXT,
          |    win_or_lose INTEGER,
          |    memo TEXT
          |)
          """.trimMargin(), 0)
      return QueryResult.Unit
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
      vararg callbacks: AfterVersion,
    ): QueryResult.Value<Unit> = QueryResult.Unit
  }
}

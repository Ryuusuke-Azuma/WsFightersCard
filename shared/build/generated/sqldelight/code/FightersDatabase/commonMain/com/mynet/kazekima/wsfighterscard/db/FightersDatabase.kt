package com.mynet.kazekima.wsfighterscard.db

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.mynet.kazekima.wsfighterscard.db.shared.newInstance
import com.mynet.kazekima.wsfighterscard.db.shared.schema
import kotlin.Unit

public interface FightersDatabase : Transacter {
  public val fightersDatabaseQueries: FightersDatabaseQueries

  public companion object {
    public val Schema: SqlSchema<QueryResult.Value<Unit>>
      get() = FightersDatabase::class.schema

    public operator fun invoke(driver: SqlDriver): FightersDatabase =
        FightersDatabase::class.newInstance(driver)
  }
}

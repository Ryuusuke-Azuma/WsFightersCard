package com.mynet.kazekima.wsfighterscard.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Long
import kotlin.String

public class FightersDatabaseQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAllGames(mapper: (
    id: Long,
    game_name: String?,
    game_date: String?,
    battle_deck: String?,
    memo: String?,
  ) -> T): Query<T> = Query(-432_306_180, arrayOf("game"), driver, "FightersDatabase.sq",
      "selectAllGames", "SELECT * FROM game") { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1),
      cursor.getString(2),
      cursor.getString(3),
      cursor.getString(4)
    )
  }

  public fun selectAllGames(): Query<Game> = selectAllGames { id, game_name, game_date, battle_deck,
      memo ->
    Game(
      id,
      game_name,
      game_date,
      battle_deck,
      memo
    )
  }

  public fun <T : Any> selectScoresForGame(game_id: Long, mapper: (
    id: Long,
    game_id: Long,
    matching_deck: String?,
    win_or_lose: Long?,
    memo: String?,
  ) -> T): Query<T> = SelectScoresForGameQuery(game_id) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getLong(1)!!,
      cursor.getString(2),
      cursor.getLong(3),
      cursor.getString(4)
    )
  }

  public fun selectScoresForGame(game_id: Long): Query<Score> = selectScoresForGame(game_id) { id,
      game_id_, matching_deck, win_or_lose, memo ->
    Score(
      id,
      game_id_,
      matching_deck,
      win_or_lose,
      memo
    )
  }

  public fun insertGame(
    game_name: String?,
    game_date: String?,
    battle_deck: String?,
    memo: String?,
  ) {
    driver.execute(-1_601_625_205, """
        |INSERT INTO game(game_name, game_date, battle_deck, memo)
        |VALUES (?, ?, ?, ?)
        """.trimMargin(), 4) {
          bindString(0, game_name)
          bindString(1, game_date)
          bindString(2, battle_deck)
          bindString(3, memo)
        }
    notifyQueries(-1_601_625_205) { emit ->
      emit("game")
    }
  }

  public fun deleteGame(id: Long) {
    driver.execute(-803_201_667, """DELETE FROM game WHERE id = ?""", 1) {
          bindLong(0, id)
        }
    notifyQueries(-803_201_667) { emit ->
      emit("game")
    }
  }

  public fun insertScore(
    game_id: Long,
    matching_deck: String?,
    win_or_lose: Long?,
    memo: String?,
  ) {
    driver.execute(1_900_370_457, """
        |INSERT INTO score(game_id, matching_deck, win_or_lose, memo)
        |VALUES (?, ?, ?, ?)
        """.trimMargin(), 4) {
          bindLong(0, game_id)
          bindString(1, matching_deck)
          bindLong(2, win_or_lose)
          bindString(3, memo)
        }
    notifyQueries(1_900_370_457) { emit ->
      emit("score")
    }
  }

  private inner class SelectScoresForGameQuery<out T : Any>(
    public val game_id: Long,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("score", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("score", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_911_440_414, """SELECT * FROM score WHERE game_id = ?""", mapper, 1) {
      bindLong(0, game_id)
    }

    override fun toString(): String = "FightersDatabase.sq:selectScoresForGame"
  }
}

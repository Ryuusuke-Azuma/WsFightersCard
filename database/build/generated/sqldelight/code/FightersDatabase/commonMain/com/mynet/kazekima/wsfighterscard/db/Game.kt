package com.mynet.kazekima.wsfighterscard.db

import kotlin.Long
import kotlin.String

public data class Game(
  public val id: Long,
  public val game_name: String?,
  public val game_date: String?,
  public val battle_deck: String?,
  public val memo: String?,
)

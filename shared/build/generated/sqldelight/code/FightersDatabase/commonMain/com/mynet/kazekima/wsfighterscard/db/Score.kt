package com.mynet.kazekima.wsfighterscard.db

import kotlin.Long
import kotlin.String

public data class Score(
  public val id: Long,
  public val game_id: Long,
  public val matching_deck: String?,
  public val win_or_lose: Long?,
  public val memo: String?,
)

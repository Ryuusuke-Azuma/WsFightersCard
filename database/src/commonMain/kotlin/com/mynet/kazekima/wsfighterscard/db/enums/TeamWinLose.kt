/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.db.enums

enum class TeamWinLose(val id: Long, val label: String) {
    LOSE(0, "Team Lose"),
    WIN(1, "Team Win");

    companion object {
        fun fromId(id: Long): TeamWinLose = entries.find { it.id == id } ?: LOSE
    }
}

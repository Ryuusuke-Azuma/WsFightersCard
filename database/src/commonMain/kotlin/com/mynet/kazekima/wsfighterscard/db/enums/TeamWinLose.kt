/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.db.enums

enum class TeamWinLose(val id: Long, val label: String, val winLose: WinLose) {
    LOSE_0_3(0, "0-3", WinLose.LOSE),
    LOSE_1_2(1, "1-2", WinLose.LOSE),
    WIN_2_1(2, "2-1", WinLose.WIN),
    WIN_3_0(3, "3-0", WinLose.WIN);

    companion object {
        fun fromId(id: Long): TeamWinLose? = entries.find { it.id == id }
    }
}

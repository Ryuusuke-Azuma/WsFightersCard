/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.db.enums

enum class TeamWinLose(val id: Long, val label: String, val winLose: WinLose) {
    WIN_3_0(1, "3-0", WinLose.WIN),
    WIN_2_1(2, "2-1", WinLose.WIN),
    LOSE_1_2(3, "1-2", WinLose.LOSE),
    LOSE_0_3(4, "0-3", WinLose.LOSE);

    companion object {
        fun fromId(id: Long): TeamWinLose? = entries.find { it.id == id }
    }
}

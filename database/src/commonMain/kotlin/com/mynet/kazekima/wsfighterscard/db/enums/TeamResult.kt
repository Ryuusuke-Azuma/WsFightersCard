/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.db.enums

enum class TeamResult(val id: Long, val label: String) {
    WIN_3_0(1, "3-0"),
    WIN_2_1(2, "2-1"),
    LOSE_1_2(3, "1-2"),
    LOSE_0_3(4, "0-3");

    companion object {
        fun fromId(id: Long): TeamResult? = entries.find { it.id == id }
    }
}

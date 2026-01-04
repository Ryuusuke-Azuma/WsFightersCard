/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.db.enums

enum class GameStyle(val id: Long, val label: String) {
    SINGLES(0, "Neos"),
    TEAMS(1, "Trio");

    companion object {
        fun fromId(id: Long): GameStyle = entries.find { it.id == id } ?: SINGLES
    }
}

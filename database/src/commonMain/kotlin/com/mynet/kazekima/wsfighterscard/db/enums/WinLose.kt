/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.db.enums

enum class WinLose(val id: Long, val label: String) {
    LOSE(0, "Lose"),
    WIN(1, "Win");

    companion object {
        fun fromId(id: Long): WinLose = entries.find { it.id == id } ?: LOSE
    }
}

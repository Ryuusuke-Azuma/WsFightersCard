/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.db.enums

enum class FirstSecond(val id: Long, val label: String) {
    FIRST(0, "1st"),
    SECOND(1, "2nd");

    companion object {
        fun fromId(id: Long): FirstSecond {
            return entries.find { it.id == id } ?: FIRST
        }
    }
}

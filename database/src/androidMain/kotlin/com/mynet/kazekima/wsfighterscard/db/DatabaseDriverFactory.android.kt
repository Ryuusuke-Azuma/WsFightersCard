/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(FightersDatabase.Schema, context, "fighters.db")
    }
}

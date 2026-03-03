/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        // SQLDelight 2.0+ handles migrations automatically when the Schema is passed.
        // It uses its own internal callback to call Schema.migrate() if needed.
        return AndroidSqliteDriver(
            schema = FightersDatabase.Schema,
            context = context,
            name = "fighters.db"
        )
    }
}

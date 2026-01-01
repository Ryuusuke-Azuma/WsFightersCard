/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(FightersDatabase.Schema, "fighters.db")
    }
}

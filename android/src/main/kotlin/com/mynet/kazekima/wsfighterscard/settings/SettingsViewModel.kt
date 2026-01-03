/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mynet.kazekima.wsfighterscard.db.DatabaseDriverFactory
import com.mynet.kazekima.wsfighterscard.db.FightersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FightersRepository(DatabaseDriverFactory(application))

    fun importFromStream(inputStream: InputStream, onComplete: (count: Int) -> Unit) {
        viewModelScope.launch {
            val gamesToInsert = mutableListOf<Triple<String, String, String>>()
            
            withContext(Dispatchers.IO) {
                inputStream.bufferedReader().use { reader ->
                    reader.lineSequence().forEach { row ->
                        if (row.isNotBlank()) {
                            val columns = row.split(",")
                            if (columns.size >= 2) {
                                val name = columns[0].trim()
                                val date = columns[1].trim()
                                val memo = if (columns.size >= 3) columns[2].trim() else ""
                                gamesToInsert.add(Triple(name, date, memo))
                            }
                        }
                    }
                }

                if (gamesToInsert.isNotEmpty()) {
                    repository.addGames(gamesToInsert)
                }
            }
            onComplete(gamesToInsert.size)
        }
    }
}

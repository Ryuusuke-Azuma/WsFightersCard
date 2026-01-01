/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard

import android.app.Application
import android.database.Cursor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mynet.kazekima.wsfighterscard.data.FightersDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecentResultsViewModel(application: Application) : AndroidViewModel(application) {

    private val _cursor = MutableLiveData<Cursor?>()
    val cursor: LiveData<Cursor?> = _cursor

    fun loadData() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                getApplication<Application>().contentResolver.query(
                    FightersDb.Game.CONTENT_URI,
                    null, null, null, null
                )
            }
            _cursor.value = result
        }
    }

    override fun onCleared() {
        super.onCleared()
        _cursor.value?.close()
    }
}

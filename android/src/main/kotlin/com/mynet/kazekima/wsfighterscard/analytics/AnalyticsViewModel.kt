/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.time.LocalDate

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val _startDate = MutableLiveData(LocalDate.now().withDayOfYear(1))
    val startDate: LiveData<LocalDate> = _startDate

    private val _endDate = MutableLiveData(LocalDate.now().withMonth(12).withDayOfMonth(31))
    val endDate: LiveData<LocalDate> = _endDate

    fun setDateRange(start: LocalDate, end: LocalDate) {
        _startDate.value = start
        _endDate.value = end
    }
}

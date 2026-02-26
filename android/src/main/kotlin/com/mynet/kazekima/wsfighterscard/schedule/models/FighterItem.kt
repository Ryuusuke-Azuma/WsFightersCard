/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.schedule.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FighterItem(
    val id: Long,
    val name: String
) : Parcelable

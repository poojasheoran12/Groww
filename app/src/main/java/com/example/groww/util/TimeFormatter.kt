package com.example.groww.util

import android.text.format.DateUtils

object TimeFormatter {
    fun getRelativeTimeSpan(timestamp: Long): String {
        if (timestamp <= 0L) return "Never updated"
        
        val now = System.currentTimeMillis()
        return DateUtils.getRelativeTimeSpanString(
            timestamp,
            now,
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE
        ).toString()
    }
}

package com.to.markdownnote.core.common.util

import android.content.Context
import android.text.format.DateUtils.isToday
import com.to.markdownnote.core.common.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
val dateFormatExcludeYear = SimpleDateFormat("MM/dd", Locale.getDefault())
val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
val dateTimeFormatForFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

private val dayOfWeekStringIds = listOf(
    R.string.sunday, R.string.monday, R.string.tuesday, R.string.wednesday,
    R.string.thursday, R.string.friday, R.string.saturday
)

fun nowTimestampMillis(): Long = System.currentTimeMillis()

fun nowTimestampSec(): Long = millisToSec(nowTimestampMillis())

fun nowTimestampForFileName(): String = dateTimeFormatForFileName.format(Date(nowTimestampMillis()))

fun getFormattedDateTime(context: Context, timestampSec: Long): Pair<String, String> {
    val date = Date(secToMillis(timestampSec))

    val dateStr = when {
        isToday(date) -> context.getString(R.string.today)
        isYesterday(date) -> context.getString(R.string.yesterday)
        isThisYear(date) -> "${dateFormatExcludeYear.format(date)}(${getDayOfWeek(context, date)})"
        else -> "${dateFormat.format(date)}(${getDayOfWeek(context, date)})"
    }

    return Pair(dateStr, timeFormat.format(date))
}

fun isToday(date: Date): Boolean = isToday(date.time)

fun isYesterday(date: Date): Boolean = isToday(date.time + 86_400_000L)

fun isThisYear(date: Date): Boolean {
    val thisYear = Calendar.getInstance().get(Calendar.YEAR)
    val cal = Calendar.getInstance().also { it.time = date }
    return cal.get(Calendar.YEAR) == thisYear
}

fun getDayOfWeek(context: Context, date: Date): String {
    val cal = Calendar.getInstance().also { it.time = date }
    return context.getString(dayOfWeekStringIds[cal.get(Calendar.DAY_OF_WEEK) - 1])
}

fun millisToSec(timestampMillis: Long): Long = timestampMillis / 1000

fun secToMillis(timestampSec: Long): Long = timestampSec * 1000

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

fun nowTimestampSec(): Long = nowTimestampMillis().toSec()

fun nowTimestampForFileName(): String = dateTimeFormatForFileName.format(Date(nowTimestampMillis()))

fun Long.toFormattedDateTime(context: Context): Pair<String, String> {
    val date = Date(toMillis())

    val dateStr = when {
        date.isToday() -> context.getString(R.string.today)
        date.isYesterday() -> context.getString(R.string.yesterday)
        date.isThisYear() -> "${dateFormatExcludeYear.format(date)}(${date.getDayOfWeek(context)})"
        else -> "${dateFormat.format(date)}(${date.getDayOfWeek(context)})"
    }

    return Pair(dateStr, timeFormat.format(date))
}

fun Date.isToday(): Boolean = isToday(time)

fun Date.isYesterday(): Boolean = isToday(time + 86_400_000L)

fun Date.isThisYear(): Boolean {
    val thisYear = Calendar.getInstance().get(Calendar.YEAR)
    val cal = Calendar.getInstance().also { it.time = this }
    return cal.get(Calendar.YEAR) == thisYear
}

fun Date.getDayOfWeek(context: Context): String {
    val cal = Calendar.getInstance().also { it.time = this }
    return context.getString(dayOfWeekStringIds[cal.get(Calendar.DAY_OF_WEEK) - 1])
}

private fun Long.toSec(): Long = this / 1000

private fun Long.toMillis(): Long = this * 1000

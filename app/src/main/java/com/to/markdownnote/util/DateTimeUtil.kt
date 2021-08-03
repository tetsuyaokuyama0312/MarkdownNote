package com.to.markdownnote.util

import android.content.Context
import android.text.format.DateUtils.isToday
import com.to.markdownnote.R
import java.text.SimpleDateFormat
import java.util.*

/** 1日あたりの経過ミリ秒: 1000 * 60 * 60 * 24 = 86400000 */
const val millisPerDay = 1000 * 60 * 60 * 24

/** 日付フォーマット: yyyy/MM/dd */
val dateFormat = SimpleDateFormat("yyyy/MM/dd")

/** 日付フォーマット: MM/dd */
val dateFormatExcludeYear = SimpleDateFormat("MM/dd")

/** 時刻フォーマット: HH:mm */
val timeFormat = SimpleDateFormat("HH:mm")

val dateTimeFormatForFileName = SimpleDateFormat("yyyyMMdd_HHmmss")

/** 曜日のR値リスト */
val dayOfWeeks = listOf(
    R.string.sunday, R.string.monday, R.string.tuesday, R.string.wednesday,
    R.string.thursday, R.string.friday, R.string.saturday
)

/**
 * 現在時刻をUNIX時間(秒)で取得する。
 *
 * @return 現在時刻のUNIX時間(秒)
 */
fun nowTimestampMillis(): Long = System.currentTimeMillis()

/**
 * 現在時刻をUNIX時間(秒)で取得する。
 *
 * @return 現在時刻のUNIX時間(秒)
 */
fun nowTimestampSec(): Long = millisToSec(nowTimestampMillis())

/**
 * 現在時刻をUNIX時間(秒)で取得する。
 *
 * @return 現在時刻のUNIX時間(秒)
 */
fun nowTimestampForFileName(): String {
    val date = Date(nowTimestampMillis())
    return dateTimeFormatForFileName.format(date)
}

/**
 * timestampから、日付を表す文字列と時刻を表す文字列を取得する。
 *
 * @param context コンテキスト
 * @param timestampSec タイムスタンプ(秒)
 * @return 日付文字列と時刻文字列のペア
 */
fun getFormattedDateTime(context: Context, timestampSec: Long): Pair<String, String> {
    val date = Date(secToMillis(timestampSec))

    val dateStr = when {
        // 今日
        isToday(date) -> context.getString(R.string.today)
        // 昨日
        isYesterday(date) -> context.getString(R.string.yesterday)
        // 今年
        isThisYear(date) -> "${dateFormatExcludeYear.format(date)}(${getDayOfWeek(context, date)})"
        // 昨年以前
        else -> "${dateFormat.format(date)}(${getDayOfWeek(context, date)})"
    }

    val timeStr = timeFormat.format(date)

    return Pair(dateStr, timeStr)
}

/**
 * 指定されたdateが今日を表しているかを判定する。
 *
 * @param date 判定対象のdate
 * @return 今日であればtrue
 */
fun isToday(date: Date): Boolean {
    return isToday(date.time)
}

/**
 * 指定されたdateが昨日を表しているかを判定する。
 *
 * @param date 判定対象のdate
 * @return 昨日であればtrue
 */
fun isYesterday(date: Date): Boolean {
    // 1日足して今日なら昨日
    return isToday(date.time + millisPerDay)
}

/**
 * 指定されたdateが今年の日付を表しているかを判定する。
 *
 * @param date 判定対象のdate
 * @return 今年の日付であればtrue
 */
fun isThisYear(date: Date): Boolean {
    val cal = Calendar.getInstance()
    cal.time = Date()
    val thisYear = cal.get(Calendar.YEAR)

    cal.time = date
    val targetYear = cal.get(Calendar.YEAR)

    return targetYear == thisYear
}

/**
 * 曜日を表す文字列を取得する。
 *
 * @param context コンテキスト
 * @param date 曜日取得対象のdate
 * @return 曜日文字列
 */
fun getDayOfWeek(context: Context, date: Date): String {
    val cal = Calendar.getInstance()
    cal.time = date
    val dayOfWeek = dayOfWeeks[cal.get(Calendar.DAY_OF_WEEK) - 1]
    return context.getString(dayOfWeek)
}

/**
 * ミリ秒を秒に変換する。
 *
 * @param timestampMillis タイムスタンプ(ミリ秒)
 */
fun millisToSec(timestampMillis: Long): Long = timestampMillis / 1000

/**
 * 秒をミリ秒に変換する。
 *
 * @param timestampSec タイムスタンプ(秒)
 */
fun secToMillis(timestampSec: Long): Long = timestampSec * 1000
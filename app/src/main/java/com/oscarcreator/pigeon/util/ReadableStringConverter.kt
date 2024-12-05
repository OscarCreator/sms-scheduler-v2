package com.oscarcreator.pigeon.util

import android.content.Context
import com.oscarcreator.pigeon.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Converts the calendar object to string with both time and date.
 *
 * Ex. 12:30 Today, 12:30 Tomorrow, 12:30 12/9
 *
 * @param context the context
 * @param currentTime current time as a Calendar object
 * @param locale the locale for displaying correct time and date
 *
 * @return string with both time and date.
 * */
fun Calendar.dateToText(
    context: Context,
    currentTime: Calendar = Calendar.getInstance(),
    locale: Locale = Locale.getDefault(),
): String {

    val dateFormat = SimpleDateFormat("d/M", locale)
    var date = dateFormat.format(this.time)

    if (this.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR)) {
        if (this.get(Calendar.DAY_OF_MONTH) == currentTime.get(Calendar.DAY_OF_MONTH)) {
            date = context.resources.getString(R.string.today)
        }
        this.add(Calendar.DAY_OF_MONTH, -1)
        if (this.get(Calendar.DAY_OF_MONTH) == currentTime.get(Calendar.DAY_OF_MONTH)){
            date = context.resources.getString(R.string.tomorrow)
        }
        this.add(Calendar.DAY_OF_MONTH, 1)
    }

    val timeFormat = SimpleDateFormat("H:mm", locale)
    val time = timeFormat.format(this.time)

    return "$time $date".trim()
}


fun Long.toTimeTemplateText(): String {
    val isNegative = this < 0
    //remove sign of millis
    var tempMillis = maxOf(this, -this)
    val days = tempMillis / 86_400_000
    //remove days as millis
    tempMillis -= days * 86_400_000
    val hours = tempMillis / 3_600_000
    //remove hours as millis
    tempMillis -= hours * 3_600_000
    val minutes = tempMillis / 60_000

    val stringBuilder = StringBuilder()

    if (isNegative) {
        stringBuilder.append("-")
    }
    if (days > 0) {
        stringBuilder.append(days).append("d").append(" ")
    }
    if (hours > 0) {
        stringBuilder.append(hours).append("h").append(" ")
    }

    if (minutes > 0) {
        stringBuilder.append(minutes).append("m")
    }

    return stringBuilder.toString().trim()
}
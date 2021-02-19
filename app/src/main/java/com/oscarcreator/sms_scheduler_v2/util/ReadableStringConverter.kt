package com.oscarcreator.sms_scheduler_v2.util

import android.content.Context
import com.oscarcreator.sms_scheduler_v2.R
import java.text.SimpleDateFormat
import java.util.*

fun Calendar.dateToText(context: Context, currentTime: Calendar, locale: Locale = Locale.getDefault() ): String {

    if (this.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR)){
        if (this.get(Calendar.DAY_OF_MONTH) == currentTime.get(Calendar.DAY_OF_MONTH)){
            return context.resources.getString(R.string.today)
        }

        //check if this Calendar is one day ahead of currentTime
        this.add(Calendar.DAY_OF_MONTH, -1)
        if (this.get(Calendar.DAY_OF_MONTH) == currentTime.get(Calendar.DAY_OF_MONTH)){
            this.add(Calendar.DAY_OF_MONTH, 1)
            return context.resources.getString(R.string.tomorrow)
        }
    }

    val dateFormat = SimpleDateFormat("E, d/M", locale)

    return dateFormat.format(this.time)
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

    if(isNegative){
        stringBuilder.append("-")
    }
    if (days > 0){
        stringBuilder.append(days).append("d").append(" ")
    }
    if (hours > 0){
        stringBuilder.append(hours).append("h").append(" ")
    }

    if (minutes > 0){
        stringBuilder.append(minutes).append("m")
    }

    return stringBuilder.toString().trim()
}
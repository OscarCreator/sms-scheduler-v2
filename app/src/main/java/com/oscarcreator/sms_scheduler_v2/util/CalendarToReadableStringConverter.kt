package com.oscarcreator.sms_scheduler_v2.util

import android.content.Context
import com.oscarcreator.sms_scheduler_v2.R
import java.text.SimpleDateFormat
import java.util.*

fun Calendar.dateToText(context: Context, currentTime: Calendar, locale: Locale = Locale.getDefault() ): String {

    if (this.get(Calendar.DAY_OF_MONTH) == currentTime.get(Calendar.DAY_OF_MONTH)){
        return context.resources.getString(R.string.today)
    }

    //check if this Calendar is one day ahead of currentTime
    this.add(Calendar.DAY_OF_MONTH, -1)
    if (this.get(Calendar.DAY_OF_MONTH) == currentTime.get(Calendar.DAY_OF_MONTH)){
        return context.resources.getString(R.string.tomorrow)
    }
    //revert
    this.add(Calendar.DAY_OF_MONTH, 1)
    val dateFormat = SimpleDateFormat("E, d/M", locale)

    return dateFormat.format(this.time)
}
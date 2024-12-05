package com.oscarcreator.pigeon.util

import androidx.room.TypeConverter
import com.oscarcreator.pigeon.data.scheduled.TreatmentStatus
import java.util.*

class Converters {
    @TypeConverter
    fun calendarToDatestamp(calendar: Calendar?): Long? = calendar?.timeInMillis

    @TypeConverter
    fun datestampToCalendar(value: Long?): Calendar? {
        return if (value != null)
            Calendar.getInstance().apply { timeInMillis = value }
        else null
    }

    @TypeConverter
    fun treatmentStatusToCode(status: TreatmentStatus): Int{
        return status.code
    }

    @TypeConverter
    fun codeToTreatmentStatus(code: Int): TreatmentStatus {
        return when (code){
            -1 -> TreatmentStatus.SCHEDULED
            0 -> TreatmentStatus.DONE
            1 -> TreatmentStatus.NOT_ARRIVED
            2 -> TreatmentStatus.CANCELED
            else -> throw EnumConstantNotPresentException(TreatmentStatus::class.java, "code: $code")
        }
    }

}
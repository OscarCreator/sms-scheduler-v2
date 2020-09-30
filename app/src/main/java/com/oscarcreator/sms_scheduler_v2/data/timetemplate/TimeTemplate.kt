package com.oscarcreator.sms_scheduler_v2.data.timetemplate

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_template")
data class TimeTemplate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    /** Delay in millis. Negative value ahead and positive after treatment time */
    val delay: Long
)
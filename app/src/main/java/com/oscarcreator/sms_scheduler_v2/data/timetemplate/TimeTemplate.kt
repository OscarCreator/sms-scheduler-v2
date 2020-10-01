package com.oscarcreator.sms_scheduler_v2.data.timetemplate

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oscarcreator.sms_scheduler_v2.data.message.Message

/**
 * A [TimeTemplate] is a object which keeps track on when to send
 * a [Message] relative to the treatment time.
 * */
@Entity(tableName = "time_template")
data class TimeTemplate(
    /**
     * The id of the [TimeTemplate]. SQLite will generate if not specified
     * */
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    /**
     * Delay in millis. Negative value ahead and positive after treatment time
     * */
    val delay: Long
)
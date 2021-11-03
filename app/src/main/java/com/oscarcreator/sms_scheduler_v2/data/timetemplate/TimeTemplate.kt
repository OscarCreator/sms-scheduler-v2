package com.oscarcreator.sms_scheduler_v2.data.timetemplate

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import java.util.*

/**
 * A [TimeTemplate] is a object which keeps track on when to send
 * a [Message] relative to the treatment time.
 * */
@Entity(tableName = "time_templates")
data class TimeTemplate(

    /**
     * Delay in millis. Negative value ahead and positive after treatment time
     * */
    val delay: Long,

    /**
     * If the [TimeTemplate] should be deleted but has references mark this as true.
     * */
    @ColumnInfo(name = "to_be_deleted") val toBeDeleted: Boolean = false,

    /**
     * The id of the [TimeTemplate]. SQLite will generate if not specified
     * */
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "time_template_id") val timeTemplateId: Long = 0,

    /**
     * The version edited [TimeTemplate]
     * */
    @ColumnInfo(name = "time_template_version") val timeTemplateVersion: Long = 0,

    /**
     * Id of the group of [TimeTemplate]s. All edited timetemplates with references will have same group id.
     * */
    @ColumnInfo(name = "time_template_group_id") val timeTemplateGroupId: String = UUID.randomUUID().toString(),

    /**
     * Time which the [TimeTemplate] is created.
     * */
    @ColumnInfo(name = "time_template_created") val timeTemplateCreated: Calendar = Calendar.getInstance()

)
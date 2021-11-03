package com.oscarcreator.sms_scheduler_v2.data.scheduled

import androidx.room.*
import com.oscarcreator.sms_scheduler_v2.data.contact.Contact
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import java.util.*

/**
 * A [ScheduledTreatment] is a entity which keeps track on what [TimeTemplate],
 * [Message] and [Treatment] will be used when sending the message.
 * */
@Entity(
    tableName = "scheduled_treatment",
    foreignKeys = [
        ForeignKey(
            entity = Treatment::class,
            parentColumns = ["treatment_id"],
            childColumns = ["treatment_id"]
        ),
        ForeignKey(
            entity = TimeTemplate::class,
            parentColumns = ["time_template_id"],
            childColumns = ["time_template_id"]
        ),
        ForeignKey(
            entity = Message::class,
            parentColumns = ["id"],
            childColumns = ["message_id"]
        )
    ],
    indices = [
        Index("scheduled_treatment_id"),
        Index("treatment_id"),
        Index("time_template_id"),
        Index("message_id")
    ]
)
data class ScheduledTreatment(
    /** id of the scheduled treatment */
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "scheduled_treatment_id") val id: Long = 0,

    /** id of the associated [Treatment] */
    @ColumnInfo(name = "treatment_id") val treatmentId: Long,

    /** time which the treatment is booked */
    @ColumnInfo(name = "treatment_time") val treatmentTime: Calendar,

    /** [TimeTemplate] which modifies the send time */
    @ColumnInfo(name = "time_template_id") val timeTemplateId: Long,

    /** id of [Message] to be sent to the [Contact]s */
    @ColumnInfo(name = "message_id") val messageId: Long,

    @ColumnInfo(name = "sms_status") var smsStatus: SmsStatus = SmsStatus.SCHEDULED,

    /** status of the treatment */
    @ColumnInfo(name = "treatment_status") var treatmentStatus: TreatmentStatus = TreatmentStatus.SCHEDULED,

    /** cause of canceled treatment */
    var cause: String? = null,

    )

/**
 * The status of the scheduled treatment
 * */
enum class TreatmentStatus(val code: Int) {

    /** Treatment is scheduled and will be started soon */
    SCHEDULED(-1),
    /** Treatment is completed */
    DONE(0),
    /** The contact did not arrive */
    NOT_ARRIVED(1),
    /** The treatment is canceled */
    CANCELED(2);
}

/**
 * Status of the sms
 * */
enum class SmsStatus(val code: Int) {
    /** SMS was not sent because of error */
    ERROR(-1),
    /** SMS is scheduled */
    SCHEDULED(0),
    /** SMS is sent to receiver */
    SENT(1),
    /** SMS is received by the receiver */
    RECEIVED(2),

}


/**
 * A data class which combines the relations with the [ScheduledTreatment]
 * */
data class ScheduledTreatmentWithMessageAndTimeTemplateAndContacts(
    /**
     * The [ScheduledTreatment]
     * */
    @Embedded val scheduledTreatment: ScheduledTreatment,
    /**
     * The [Message] associated with the [ScheduledTreatment]
     * */
    @Relation(
        parentColumn = "message_id",
        entityColumn = "id",
    )
    val message: Message,
    /**
     * The [TimeTemplate] associated with the [ScheduledTreatment]
     */
    @Relation(
        parentColumn = "time_template_id",
        entityColumn = "time_template_id"
    )
    val timeTemplate: TimeTemplate,
    /**
     * The [Treatment] associated with the [ScheduledTreatment]
     */
    @Relation(
        parentColumn = "treatment_id",
        entityColumn = "treatment_id"
    )
    val treatment: Treatment,
    /**
     * The [Contact]s which will receive a notification sms
     */
    @Relation(
        parentColumn = "scheduled_treatment_id",
        entityColumn = "contact_id",
        associateBy = Junction(ScheduledTreatmentContactCrossRef::class)
    )
    val contacts: List<Contact>

){

    /**
     * @return the time which the message is supposed to be sent
     * */
    fun getSendTime(): Long {
        return scheduledTreatment.treatmentTime.timeInMillis + timeTemplate.delay
    }

    /**
     * Returns the formatted messages
     */
    fun getFormattedMessages(): List<String> {
        val messages = contacts.map { message.message }
        //TODO format
        return messages
    }

    /**
     * Returns all the phone numbers
     */
    fun getPhoneNumbers(): List<String> {
        return contacts.map { it.phoneNumber  }
    }

}

/**
 * Relation between [ScheduledTreatment] and [Contact]
 */
@Entity(
    tableName = "scheduled_treatment_cross_ref",
    primaryKeys = [
        "scheduled_treatment_id",
        "contact_id"
    ],
    foreignKeys = [
        ForeignKey(
            entity = ScheduledTreatment::class,
            parentColumns = ["scheduled_treatment_id"],
            childColumns = ["scheduled_treatment_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["contact_id"],
            childColumns = ["contact_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class ScheduledTreatmentContactCrossRef(
    @ColumnInfo(name = "scheduled_treatment_id") val scheduledTreatmentId: Long,
    @ColumnInfo(name = "contact_id") val contactId: Long,
    /** Time of the delivered */
    @ColumnInfo(name = "delivered_time") val deliveredTime: Calendar? = null
)


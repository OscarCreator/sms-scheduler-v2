package com.oscarcreator.pigeon.data.scheduled

import androidx.room.*
import com.oscarcreator.pigeon.data.contact.Contact
import com.oscarcreator.pigeon.data.message.Message
import com.oscarcreator.pigeon.data.timetemplate.TimeTemplate
import com.oscarcreator.pigeon.data.treatment.Treatment
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
            parentColumns = ["message_id"],
            childColumns = ["message_id"]
        ),
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["contact_id"],
            childColumns = ["contact_id"]
        )
    ],
    indices = [
        Index("scheduled_treatment_id"),
        Index("treatment_id"),
        Index("time_template_id"),
        Index("message_id"),
        Index("contact_id")
    ]
)
data class ScheduledTreatment(

    /** id of [Contact] to be sent to*/
    @ColumnInfo(name = "contact_id") val contactId: Long,

    /** id of the associated [Treatment] */
    @ColumnInfo(name = "treatment_id") val treatmentId: Long,

    /** id of [Message] to be sent to the [Contact] */
    @ColumnInfo(name = "message_id") val messageId: Long,

    /** [TimeTemplate] which modifies the send time */
    @ColumnInfo(name = "time_template_id") val timeTemplateId: Long,

    /** time which the treatment is booked */
    @ColumnInfo(name = "treatment_time") val treatmentTime: Calendar,

    @ColumnInfo(name = "sms_status") var smsStatus: SmsStatus = SmsStatus.SCHEDULED,

    /** status of the treatment */
    @ColumnInfo(name = "treatment_status") var treatmentStatus: TreatmentStatus = TreatmentStatus.SCHEDULED,

    /** The time the sms was sent. */
    @ColumnInfo(name = "sms_sent_time") var smsSentTime: Calendar? = null,

    /** The time the sms was delivered. */
    @ColumnInfo(name = "sms_delivered_time") var smsDeliveredTime: Calendar? = null,

    /** id of the scheduled treatment */
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "scheduled_treatment_id") val scheduledTreatmentId: Long = 0,

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
data class ScheduledTreatmentWithMessageTimeTemplateAndContact(
    /**
     * The [ScheduledTreatment]
     * */
    @Embedded val scheduledTreatment: ScheduledTreatment,
    /**
     * The [Message] associated with the [ScheduledTreatment]
     * */
    @Relation(
        parentColumn = "message_id",
        entityColumn = "message_id",
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
        parentColumn = "contact_id",
        entityColumn = "contact_id",
    )
    val contact: Contact

){

    /**
     * @return the time which the message is supposed to be sent
     * */
    fun getSendTime(): Long {
        return scheduledTreatment.treatmentTime.timeInMillis + timeTemplate.delay
    }

}



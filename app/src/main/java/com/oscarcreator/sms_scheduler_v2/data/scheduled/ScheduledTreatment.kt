package com.oscarcreator.sms_scheduler_v2.data.scheduled

import androidx.room.*
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
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
            parentColumns = ["id"],
            childColumns = ["treatment_id"]
        ),
        ForeignKey(
            entity = TimeTemplate::class,
            parentColumns = ["id"],
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

    /** id of [Message] to be sent to the [Customer]s */
    @ColumnInfo(name = "message_id") val messageId: Long,

    /** status of the treatment */
    val label: TreatmentStatus = TreatmentStatus.SCHEDULED,

    /** cause of canceled treatment */
    val cause: String? = null,

)

/**
 * The status of the scheduled treatment
 * */
enum class TreatmentStatus(val code: Int) {

    /** Treatment is scheduled and will be started soon */
    SCHEDULED(-1),
    /** Treatment is completed */
    DONE(0),
    /** The customer did not arrive */
    NOT_ARRIVED(1),
    /** The treatment is canceled */
    CANCELED(2);
}

/**
 * A data class which combines the relations with the [ScheduledTreatment]
 * */
data class ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers(
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
        entityColumn = "id"
    )
    val timeTemplate: TimeTemplate,
    /**
     * The [Treatment] associated with the [ScheduledTreatment]
     */
    @Relation(
        parentColumn = "treatment_id",
        entityColumn = "id"
    )
    val treatment: Treatment,
    /**
     * The [Customer]s which will receive a notification sms
     */
    @Relation(
        parentColumn = "scheduled_treatment_id",
        entityColumn = "customer_id",
        associateBy = Junction(ScheduledTreatmentCustomerCrossRef::class)
    )
    val customers: List<Customer>

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
        val messages = customers.map { message.message }
        //TODO format
        return messages
    }

    /**
     * Returns all the phone numbers
     */
    fun getPhoneNumbers(): List<String> {
        return customers.map { it.phoneNumber  }
    }

}

/**
 * Relation between [ScheduledTreatment] and [Customer]
 */
@Entity(
    tableName = "scheduled_treatment_cross_ref",
    primaryKeys = [
        "scheduled_treatment_id",
        "customer_id"
    ],
    foreignKeys = [
        ForeignKey(
            entity = ScheduledTreatment::class,
            parentColumns = ["scheduled_treatment_id"],
            childColumns = ["scheduled_treatment_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["customer_id"],
            childColumns = ["customer_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class ScheduledTreatmentCustomerCrossRef(
    @ColumnInfo(name = "scheduled_treatment_id") val scheduledTreatmentId: Long,
    @ColumnInfo(name = "customer_id") val customerId: Long,
    /** Time of the delivered */
    @ColumnInfo(name = "delivered_time") val deliveredTime: Calendar? = null
)


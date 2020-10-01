package com.oscarcreator.sms_scheduler_v2.data.scheduled

import androidx.room.*
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import java.util.*

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
    @PrimaryKey @ColumnInfo(name = "scheduled_treatment_id") val id: Long,

    /** id of the associated [Treatment] */
    @ColumnInfo(name = "treatment_id") val treatmentId: Long,

    /** time which the treatment is booked */
    @ColumnInfo(name = "treatment_time") val treatmentTime: Calendar,

    /** time template which modifies the send time */
    @ColumnInfo(name = "time_template_id") val timeTemplateId: Long,

    /** message id of message to be sent to the customers */
    @ColumnInfo(name = "message_id") val messageId: Long,

    /** status of the treatment */
    val label: TreatmentStatus = TreatmentStatus.SCHEDULED,

    /** cause of canceled treatment */
    val cause: String? = null,

    @ColumnInfo(name = "delivered_time") val deliveredTime: Calendar? = null
)


enum class TreatmentStatus(val code: Int) {

    SCHEDULED(-1),
    DONE(0),
    NOT_ARRIVED(1),
    CANCELED(2);

}

data class ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers(
    @Embedded val scheduledTreatment: ScheduledTreatment,
    @Relation(
        parentColumn = "message_id",
        entityColumn = "id",
    )
    val message: Message,
    @Relation(
        parentColumn = "time_template_id",
        entityColumn = "id"
    )
    val timeTemplate: TimeTemplate,
    @Relation(
        parentColumn = "treatment_id",
        entityColumn = "id"
    )
    val treatment: Treatment,
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

    fun getFormattedMessage(): String {
        TODO()
    }

    fun getPhoneNumbers(): List<String> {
        TODO()
    }

}

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
    @ColumnInfo(name = "customer_id") val customerId: Long
)


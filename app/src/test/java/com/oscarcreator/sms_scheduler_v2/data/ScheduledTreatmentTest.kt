package com.oscarcreator.sms_scheduler_v2.data

import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers
import com.oscarcreator.sms_scheduler_v2.data.scheduled.TreatmentStatus
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.util.*

class ScheduledTreatmentTest {

    @Test
    fun testDefaultValues(){
        val scheduledTreatment = ScheduledTreatment(
            treatmentId = 1,
            treatmentTime = Calendar.getInstance(),
            timeTemplateId = 2,
            messageId = 4,
        )

        assertThat(scheduledTreatment.id, `is`(0))
        assertThat(scheduledTreatment.label, `is`(TreatmentStatus.SCHEDULED))
        assertThat(scheduledTreatment.cause, `is`(nullValue()))

    }

    @Test
    fun getSendTime_is_treatmentTimePlusDelay(){
        val scheduledTreatment = ScheduledTreatment(
            treatmentId = 1,
            treatmentTime = Calendar.getInstance(),
            timeTemplateId = 2,
            messageId = 4,
        )
        val message = Message(message = "some text")
        val timeTemplate  = TimeTemplate(delay = 10)
        val customers = listOf(Customer(name = "Ferd", phoneNumber = "0723849064"))
        val treatment = Treatment(name = "something", price = 40, duration = 1)

        val scheduledTreatmentWithData = ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers(
            scheduledTreatment, message, timeTemplate, treatment, customers
        )

        assertThat(scheduledTreatmentWithData.getSendTime(),
            `is`(scheduledTreatment.treatmentTime.timeInMillis + timeTemplate.delay))

    }

    @Test
    fun getFormattedMessages_is_scheduledTreatmentMessage(){
        val scheduledTreatment = ScheduledTreatment(
            treatmentId = 1,
            treatmentTime = Calendar.getInstance(),
            timeTemplateId = 2,
            messageId = 4,
        )
        val message = Message(message = "some text")
        val timeTemplate  = TimeTemplate(delay = 10)
        val customers = listOf(
            Customer(name = "Ferd", phoneNumber = "0723849064"),
            Customer(name = "Ferd2", phoneNumber = "1723849065")
        )
        val treatment = Treatment(name = "something", price = 40, duration = 1)

        val scheduledTreatmentWithData = ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers(
            scheduledTreatment, message, timeTemplate, treatment, customers
        )

        assertThat(scheduledTreatmentWithData.getFormattedMessages(),
            `is`(listOf(message.message, message.message)))

    }

    @Test
    fun getPhoneNumbers_returnsAllPhoneNumbers(){
        val scheduledTreatment = ScheduledTreatment(
            treatmentId = 1,
            treatmentTime = Calendar.getInstance(),
            timeTemplateId = 2,
            messageId = 4,
        )
        val message = Message(message = "some text")
        val timeTemplate  = TimeTemplate(delay = 10)
        val customers = listOf(
            Customer(name = "Ferd", phoneNumber = "0723849064"),
            Customer(name = "Ferd2", phoneNumber = "1723849065")
        )
        val treatment = Treatment(name = "something", price = 40, duration = 1)

        val scheduledTreatmentWithData = ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers(
            scheduledTreatment, message, timeTemplate, treatment, customers
        )

        assertThat(scheduledTreatmentWithData.getPhoneNumbers(),
            `is`(listOf(customers[0].phoneNumber, customers[1].phoneNumber)))

    }



}
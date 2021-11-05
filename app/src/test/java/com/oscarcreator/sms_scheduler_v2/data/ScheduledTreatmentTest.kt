package com.oscarcreator.sms_scheduler_v2.data

import com.oscarcreator.sms_scheduler_v2.data.contact.Contact
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageTimeTemplateAndContact
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
            contactId = 3
        )

        assertThat(scheduledTreatment.id, `is`(0))
        assertThat(scheduledTreatment.treatmentStatus, `is`(TreatmentStatus.SCHEDULED))
        assertThat(scheduledTreatment.cause, `is`(nullValue()))

    }

    @Test
    fun getSendTime_is_treatmentTimePlusDelay(){
        val scheduledTreatment = ScheduledTreatment(
            treatmentId = 1,
            treatmentTime = Calendar.getInstance(),
            timeTemplateId = 2,
            messageId = 4,
            contactId = 3
        )
        val message = Message(message = "some text")
        val timeTemplate  = TimeTemplate(delay = 10)
        val customer = Contact(name = "Ferd", phoneNumber = "0723849064")
        val treatment = Treatment(name = "something", price = 40, duration = 1)

        val scheduledTreatmentWithData = ScheduledTreatmentWithMessageTimeTemplateAndContact(
            scheduledTreatment, message, timeTemplate, treatment, customer
        )

        assertThat(scheduledTreatmentWithData.getSendTime(),
            `is`(scheduledTreatment.treatmentTime.timeInMillis + timeTemplate.delay))

    }




}
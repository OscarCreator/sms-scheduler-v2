package com.oscarcreator.sms_scheduler_v2.data

import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.TreatmentStatus
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.util.*

class ScheduledTreatmentTest {

    @Test
    fun testDefaultValues(){
        val scheduledTreatment = ScheduledTreatment(
            id = 0,
            treatmentId = 1,
            treatmentTime = Calendar.getInstance(),
            timeTemplateId = 2,
            messageId = 4,
        )

        assertThat(scheduledTreatment.label, `is`(TreatmentStatus.SCHEDULED))
        assertThat(scheduledTreatment.cause, `is`(nullValue()))
        assertThat(scheduledTreatment.deliveredTime, `is`(nullValue()))

    }

}
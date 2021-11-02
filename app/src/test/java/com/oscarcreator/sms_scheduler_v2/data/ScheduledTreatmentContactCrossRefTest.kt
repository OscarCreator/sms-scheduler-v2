package com.oscarcreator.sms_scheduler_v2.data

import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentContactCrossRef
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ScheduledTreatmentContactCrossRefTest {

    @Test
    fun testDefaultValues(){
        val scheduledTreatmentContactCrossRef = ScheduledTreatmentContactCrossRef(
            scheduledTreatmentId = 1,
            contactId = 1
        )
        assertThat(scheduledTreatmentContactCrossRef.deliveredTime, `is`(nullValue()))
    }
}
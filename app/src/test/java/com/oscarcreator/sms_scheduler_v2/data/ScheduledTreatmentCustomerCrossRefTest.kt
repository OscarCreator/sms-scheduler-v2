package com.oscarcreator.sms_scheduler_v2.data

import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentCustomerCrossRef
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ScheduledTreatmentCustomerCrossRefTest {

    @Test
    fun testDefaultValues(){
        val scheduledTreatmentCustomerCrossRef = ScheduledTreatmentCustomerCrossRef(
            scheduledTreatmentId = 1,
            customerId = 1
        )
        assertThat(scheduledTreatmentCustomerCrossRef.deliveredTime, `is`(nullValue()))
    }
}
package com.oscarcreator.sms_scheduler_v2.data

import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class TreatmentTest {

    @Test
    fun testDefaultValues(){
        val treatment = Treatment(id = 1, name = "something", price = 4, duration = 2)
        assertThat(treatment.id, `is`(1))
        assertThat(treatment.name, `is`("something"))
        assertThat(treatment.price, `is`(4))
        assertThat(treatment.duration, `is`(2))
    }

}
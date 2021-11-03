package com.oscarcreator.sms_scheduler_v2.data

import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class TimeTemplateTest {

    @Test
    fun testObject(){
        val timeTemplate = TimeTemplate(timeTemplateId = 1, delay = -40)
        assertThat(timeTemplate.delay, `is`(-40))
    }

}
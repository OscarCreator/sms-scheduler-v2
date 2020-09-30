package com.oscarcreator.sms_scheduler_v2.data

import com.oscarcreator.sms_scheduler_v2.data.message.Message
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat

class MessageTest {

    @Test
    fun isTemplate_default_isFalse(){
        val message = Message(message = "temporary")
        assertThat(message.isTemplate, `is`(false))
    }

}
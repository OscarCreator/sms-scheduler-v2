package com.oscarcreator.pigeon.data

import com.oscarcreator.pigeon.data.message.Message
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class MessageTest {

    @Test
    fun isTemplate_default_isFalse(){
        val message = Message(message = "temporary")
        assertThat(message.toBeDeleted, `is`(false))
    }

}
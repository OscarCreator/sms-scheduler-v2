package com.oscarcreator.pigeon.data

import com.oscarcreator.pigeon.data.contact.Contact
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ContactTest {

    @Test
    fun testDefaultValues(){
        val customer = Contact(name = "wolf", phoneNumber = "04846454")
        assertThat(customer.money, `is`(0))
    }

}
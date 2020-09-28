package com.oscarcreator.sms_scheduler_v2.data

import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

class CustomerTest {

    @Test
    fun testDefaultValues(){
        val customer = Customer(name = "wolf", phoneNumber = "04846454")
        assertThat(customer.money, `is`(0))
    }

}
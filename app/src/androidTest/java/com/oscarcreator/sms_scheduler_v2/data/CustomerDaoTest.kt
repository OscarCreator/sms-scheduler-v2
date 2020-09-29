package com.oscarcreator.sms_scheduler_v2.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.data.customer.CustomerDao
import com.oscarcreator.sms_scheduler_v2.util.observeOnce
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.jvm.Throws

@RunWith(AndroidJUnit4::class)
class CustomerDaoTest : BaseDaoTest() {

    private lateinit var customerDao: CustomerDao

    @Before
    override fun inititalizeDatabase() {
        super.inititalizeDatabase()
        customerDao = database.customerDao()
    }

    @Test
    @Throws(Exception::class)
    fun customer_insertedInDatabase_returnsCustomer() = runBlocking {
        val customer = Customer(id = 1, name = "bengan", phoneNumber = "0754962045")
        customerDao.insert(customer)

        customerDao.getCustomers().observeOnce {
            assertThat(it, `is`(listOf(customer)))
        }
    }

    @Test
    @Throws(Exception::class)
    fun customer_insertedAndDeleted_returnsEmpty() = runBlocking {
        val customer = Customer(id = 1, name = "bergit", phoneNumber = "0738092735")
        customerDao.insert(customer)
        customerDao.delete(customer)

        customerDao.getCustomers().observeOnce {
            assertThat(it, `is`(emptyList()))
        }
    }

    @Test
    @Throws(Exception::class)
    fun customer_insertedAndUpdated_returnsUpdated() = runBlocking {
        val customer = Customer(id = 100, name = "bergit", phoneNumber = "0738092735")
        customerDao.insert(customer)
        val updatedCustomer = Customer(id = 100, name = "Bergit", phoneNumber = "0738092734")
        customerDao.update(updatedCustomer)

        customerDao.getCustomers().observeOnce {
            assertThat(it, `is`(listOf(updatedCustomer)))
        }
    }

}
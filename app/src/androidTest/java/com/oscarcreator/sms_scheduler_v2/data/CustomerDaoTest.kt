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
        assertThat(customerDao.insert(customer), `is`(1))

        customerDao.getCustomers().observeOnce {
            assertThat(it, `is`(listOf(customer)))
        }
    }

    @Test
    @Throws(Exception::class)
    fun customer_insertedAndDeleted_returnsEmpty() = runBlocking {
        val customer = Customer(id = 1, name = "bergit", phoneNumber = "0738092735")
        assertThat(customerDao.insert(customer), `is`(1))
        assertThat(customerDao.delete(customer), `is`(1))

        customerDao.getCustomers().observeOnce {
            assertThat(it, `is`(emptyList()))
        }
    }

    @Test
    @Throws(Exception::class)
    fun customer_insertedAndUpdated_returnsUpdated() = runBlocking {
        val customer = Customer(id = 100, name = "bergit", phoneNumber = "0738092735")
        assertThat(customerDao.insert(customer), `is`(100))
        val updatedCustomer = Customer(id = 100, name = "Bergit", phoneNumber = "0738092734")
        assertThat(customerDao.update(updatedCustomer), `is`(1))

        customerDao.getCustomers().observeOnce {
            assertThat(it, `is`(listOf(updatedCustomer)))
        }
    }

    @Test
    fun customer_getMatching_returnsOnlyMatching() = runBlocking {
        val customer1 = Customer(300, "bergit hansson", "18305238940")
        assertThat(customerDao.insert(customer1), `is`(customer1.id))
        val customer2 = Customer(301, "ulf hansson", "053243945")
        assertThat(customerDao.insert(customer2), `is`(customer2.id))
        val customer3 = Customer(302, "sten", "053058425")
        assertThat(customerDao.insert(customer3), `is`(customer3.id))

        assertThat(customerDao.getCustomersLike("%hansson%"), `is`(listOf(customer1, customer2)))

        assertThat(customerDao.getCustomersLike("%53%"), `is`(listOf(customer3, customer2)))

    }

}
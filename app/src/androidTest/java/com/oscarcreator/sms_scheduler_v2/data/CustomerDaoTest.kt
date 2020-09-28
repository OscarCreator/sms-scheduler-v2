package com.oscarcreator.sms_scheduler_v2.data

import android.content.Context
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.data.customer.CustomerDao
import com.oscarcreator.sms_scheduler_v2.util.observeOnce
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class CustomerDaoTest {

    @Rule
    @JvmField
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var customerDao: CustomerDao


    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        customerDao = database.customerDao()

    }

    @After
    fun closeDb() {
        countingTaskExecutorRule.drainTasks(10, TimeUnit.SECONDS)
        database.close()
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
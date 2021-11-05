package com.oscarcreator.sms_scheduler_v2.data.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oscarcreator.sms_scheduler_v2.data.contact.Contact
import com.oscarcreator.sms_scheduler_v2.data.contact.local.ContactDao
import com.oscarcreator.sms_scheduler_v2.util.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactDaoTest : BaseDaoTest() {

    private lateinit var contactDao: ContactDao

    @Before
    override fun inititalizeDatabase() {
        super.inititalizeDatabase()
        contactDao = database.contactDao()
    }

    @Test
    @Throws(Exception::class)
    fun customer_insertedInDatabase_returnsCustomer() = runBlocking {
        val customer = Contact(contactId = 1, name = "bengan", phoneNumber = "0754962045")
        assertThat(contactDao.insert(customer), `is`(1))

        assertThat(contactDao.observeAllContacts().getOrAwaitValue(), `is`(listOf(customer)))
    }

    @Test
    @Throws(Exception::class)
    fun customer_insertedAndDeleted_returnsEmpty() = runBlocking {
        val customer = Contact(contactId = 1, name = "bergit", phoneNumber = "0738092735")
        assertThat(contactDao.insert(customer), `is`(1))
        assertThat(contactDao.delete(customer), `is`(1))

        assertThat(contactDao.observeAllContacts().getOrAwaitValue(), `is`(emptyList()))
    }

    @Test
    @Throws(Exception::class)
    fun customer_insertedAndUpdated_returnsUpdated() = runBlocking {
        val customer = Contact(contactId = 100, name = "bergit", phoneNumber = "0738092735")
        assertThat(contactDao.insert(customer), `is`(100))
        val updatedCustomer = Contact(contactId = 100, name = "Bergit", phoneNumber = "0738092734")
        assertThat(contactDao.update(updatedCustomer), `is`(1))

        assertThat(contactDao.observeAllContacts().getOrAwaitValue(), `is`(listOf(updatedCustomer)))
    }

    @Test
    fun customer_getMatching_returnsOnlyMatching() = runBlocking {
        val customer1 = Contact( "bergit hansson", "18305238940", contactId = 1)
        assertThat(contactDao.insert(customer1), `is`(customer1.contactId))
        val customer2 = Contact( "ulf hansson", "053243945", contactId = 2)
        assertThat(contactDao.insert(customer2), `is`(customer2.contactId))
        val customer3 = Contact( "sten", "053058425", contactId = 3)
        assertThat(contactDao.insert(customer3), `is`(customer3.contactId))

        assertThat(contactDao.getCustomersLike("%hansson%"), `is`(listOf(customer1, customer2)))

        assertThat(contactDao.getCustomersLike("%53%"), `is`(listOf(customer3, customer2)))
    }

}
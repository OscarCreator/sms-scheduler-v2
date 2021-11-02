package com.oscarcreator.sms_scheduler_v2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.oscarcreator.sms_scheduler_v2.data.Result
import com.oscarcreator.sms_scheduler_v2.data.contact.Contact
import com.oscarcreator.sms_scheduler_v2.data.contact.ContactsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class FakeContactsRepository : ContactsRepository {

    var contactsServiceData: LinkedHashMap<Long, Contact> = LinkedHashMap()

    private val observableContacts = MutableLiveData<List<Contact>>()

    override fun observeContacts(): LiveData<List<Contact>> {
        runBlocking { observableContacts.value = contactsServiceData.values.toList() }
        return observableContacts
    }

    override suspend fun getCustomerById(customerId: Long): Result<Contact> {
        val contact = contactsServiceData[customerId]
        return if (contact == null) {
            Result.Error(Exception("Did not find contact"))
        } else {
            Result.Success(contact)
        }
    }

    override fun observeCustomer(customerId: Long): LiveData<Result<Contact>> {
        runBlocking { observableContacts.value = contactsServiceData.values.toList() }
        return observableContacts.map { customers ->
            val customer = customers.find { it.id == customerId }
            if (customer == null) {
                Result.Error(Exception("Did not find contact"))
            } else {
                Result.Success(customer)
            }
        }
    }

    override suspend fun getCustomersLike(text: String): List<Contact> {
        return contactsServiceData
            .map { it.value }
            .filter { it.name.contains(text) }
            .toList()
    }

    override suspend fun insert(contact: Contact): Long {
        contactsServiceData[contact.id] = contact
        withContext(Dispatchers.Main) {
            observableContacts.value = contactsServiceData.values.toList()
        }
        return contact.id
    }

    override suspend fun delete(vararg contact: Contact): Int {
        for (value in contact) {
            contactsServiceData.remove(value.id)
        }
        return contact.size
    }

    override suspend fun deleteById(contactId: Long): Int {
        contactsServiceData.remove(contactId)
        return 1
    }

    override suspend fun update(contact: Contact): Int {
        contactsServiceData[contact.id] = contact
        return 1
    }
}
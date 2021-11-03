package com.oscarcreator.sms_scheduler_v2.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.oscarcreator.sms_scheduler_v2.data.contact.Contact
import com.oscarcreator.sms_scheduler_v2.data.contact.ContactsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class FakeContactsRepository : ContactsRepository {

    var contactsServiceData: LinkedHashMap<Long, Contact> = LinkedHashMap()

    private val observableContacts = MutableLiveData<List<Contact>>()

    override fun observeAllContacts(): LiveData<List<Contact>> {
        runBlocking { refreshTreatments() }
        return observableContacts
    }

    override fun observeContacts(): LiveData<List<Contact>> {
        runBlocking { refreshTreatments() }
        return observableContacts
            .map { contacts ->
                contacts.filter { !it.toBeDeleted } }
    }

    override fun observeContactsASC(): LiveData<List<Contact>> {
        runBlocking { refreshTreatments() }
        return observableContacts
            .map { contacts ->
                contacts.filter { !it.toBeDeleted }.sortedBy { it.name } }
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
        runBlocking { refreshTreatments() }
        return observableContacts.map { customers ->
            val customer = customers.find { it.contactId == customerId }
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
        contactsServiceData[contact.contactId] = contact
        withContext(Dispatchers.Main) {
            refreshTreatments()
        }
        return contact.contactId
    }

    override suspend fun delete(vararg contact: Contact): Int {
        for (value in contact) {
            contactsServiceData.remove(value.contactId)
        }
        refreshTreatments()
        return contact.size
    }

    override suspend fun deleteById(contactId: Long): Int {
        contactsServiceData.remove(contactId)
        refreshTreatments()
        return 1
    }

    override suspend fun update(contact: Contact): Int {
        contactsServiceData[contact.contactId] = contact
        refreshTreatments()
        return 1
    }

    override suspend fun updateToBeDeleted(contactId: Long) {
        val contact = contactsServiceData[contactId]
        if (contact != null) {
            val newContact =  Contact(contact.name, contact.phoneNumber, contact.money, toBeDeleted = true, contactId = contact.contactId, contactGroupId = contact.contactGroupId, contactVersion = contact.contactVersion)
            contactsServiceData[contactId] = newContact
        }
    }

    private fun refreshTreatments() {
        observableContacts.value = contactsServiceData.values.toList()
    }
}
package com.oscarcreator.pigeon.data.contact.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.oscarcreator.pigeon.data.Result
import com.oscarcreator.pigeon.data.contact.Contact
import com.oscarcreator.pigeon.data.contact.ContactsDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ContactsLocalDataSource internal constructor(
    private val contactDao: ContactDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ContactsDataSource {

    override fun observeAllContacts(): LiveData<List<Contact>> = contactDao.observeAllContacts()

    override fun observeContacts(): LiveData<List<Contact>> = contactDao.observeContactsASC()

    override fun observeContactsASC(): LiveData<List<Contact>> = contactDao.observeContactsASC()

    override suspend fun getCustomer(contactId: Long): Result<Contact> = withContext(ioDispatcher) {
        try {
            val contact = contactDao.getContact(contactId)
            if (contact != null) {
                Result.Success(contact)
            } else {
                Result.Error(Exception("Contact not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun observeCustomer(customerId: Long): LiveData<Result<Contact>> {
        return contactDao.observeCustomer(customerId).map {
            Result.Success(it)
        }
    }

    override suspend fun getCustomersLike(text: String): List<Contact> = withContext(ioDispatcher) {
        contactDao.getCustomersLike("%$text%")
    }


    override suspend fun insert(contact: Contact): Long = contactDao.insert(contact)

    override suspend fun delete(vararg contact: Contact): Int = contactDao.delete(*contact)

    override suspend fun deleteById(contactId: Long): Int = contactDao.deleteById(contactId)

    override suspend fun update(contact: Contact): Int = contactDao.update(contact)

    override suspend fun updateToBeDeleted(contactId: Long) = contactDao.updateToBeDeleted(contactId)

    override suspend fun updateScheduledTreatmentsWithNewContact(oldContactId: Long, newContactId: Long) =
        contactDao.updateScheduledTreatmentsWithNewContact(oldContactId, newContactId)

    override suspend fun getContacts(): List<Contact> = contactDao.getContacts()

}
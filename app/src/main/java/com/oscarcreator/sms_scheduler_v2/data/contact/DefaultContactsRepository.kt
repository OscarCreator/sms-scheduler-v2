package com.oscarcreator.sms_scheduler_v2.data.contact

import androidx.lifecycle.LiveData
import com.oscarcreator.sms_scheduler_v2.data.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DefaultContactsRepository(
    private val contactsDataSource: ContactsDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ContactsRepository {

    override fun observeAllContacts(): LiveData<List<Contact>> = contactsDataSource.observeAllContacts()

    override fun observeContacts(): LiveData<List<Contact>> = contactsDataSource.observeContacts()

    override fun observeContactsASC(): LiveData<List<Contact>> = contactsDataSource.observeContactsASC()

    override suspend fun getContact(id: Long): Result<Contact> = contactsDataSource.getCustomer(id)

    override fun observeContact(customerId: Long): LiveData<Result<Contact>> = contactsDataSource.observeCustomer(customerId)

    override suspend fun getContactsLike(text: String): List<Contact> =
        contactsDataSource.getCustomersLike(text)

    override suspend fun insert(contact: Contact): Long = contactsDataSource.insert(contact)

    override suspend fun delete(vararg contact: Contact): Int = contactsDataSource.delete(*contact)

    override suspend fun deleteById(contactId: Long): Int = contactsDataSource.deleteById(contactId)

    override suspend fun update(contact: Contact): Int = contactsDataSource.update(contact)

    override suspend fun updateToBeDeleted(contactId: Long) = contactsDataSource.updateToBeDeleted(contactId)
}
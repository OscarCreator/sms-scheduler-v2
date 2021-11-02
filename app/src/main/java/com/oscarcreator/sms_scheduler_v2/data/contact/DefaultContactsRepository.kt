package com.oscarcreator.sms_scheduler_v2.data.contact

import androidx.lifecycle.LiveData
import com.oscarcreator.sms_scheduler_v2.data.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DefaultContactsRepository(
    private val contactsDataSource: ContactsDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ContactsRepository {

    override fun observeContacts(): LiveData<List<Contact>> = contactsDataSource.observeContacts()

    override suspend fun getCustomerById(id: Long): Result<Contact> = contactsDataSource.getCustomer(id)

    override fun observeCustomer(customerId: Long): LiveData<Result<Contact>> = contactsDataSource.observeCustomer(customerId)

    override suspend fun getCustomersLike(text: String): List<Contact> =
        contactsDataSource.getCustomersLike(text)

    override suspend fun insert(contact: Contact): Long = contactsDataSource.insert(contact)

    override suspend fun delete(vararg contact: Contact): Int = contactsDataSource.delete(*contact)

    override suspend fun deleteById(contactId: Long): Int = contactsDataSource.deleteById(contactId)

    override suspend fun update(contact: Contact): Int = contactsDataSource.update(contact)
}
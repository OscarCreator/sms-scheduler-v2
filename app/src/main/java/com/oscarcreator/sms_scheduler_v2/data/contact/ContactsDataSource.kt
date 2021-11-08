package com.oscarcreator.sms_scheduler_v2.data.contact

import androidx.lifecycle.LiveData
import com.oscarcreator.sms_scheduler_v2.data.Result

interface ContactsDataSource {

    fun observeAllContacts(): LiveData<List<Contact>>

    fun observeContacts(): LiveData<List<Contact>>

    fun observeContactsASC(): LiveData<List<Contact>>

    suspend fun getCustomer(customerId: Long): Result<Contact>

    fun observeCustomer(customerId: Long): LiveData<Result<Contact>>

    suspend fun getCustomersLike(text: String): List<Contact>

    suspend fun insert(contact: Contact): Long

    suspend fun delete(vararg contact: Contact): Int

    suspend fun deleteById(contactId: Long): Int

    suspend fun update(contact: Contact): Int

    suspend fun updateToBeDeleted(contactId: Long)

    suspend fun updateScheduledTreatmentsWithNewContact(oldContactId: Long, newContactId: Long)
}
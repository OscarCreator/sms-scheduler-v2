package com.oscarcreator.sms_scheduler_v2.data.contact

import androidx.lifecycle.LiveData
import com.oscarcreator.sms_scheduler_v2.data.Result

interface ContactsRepository {

    fun observeAllContacts(): LiveData<List<Contact>>

    fun observeContacts(): LiveData<List<Contact>>

    fun observeContactsASC(): LiveData<List<Contact>>

    suspend fun getContact(customerId: Long): Result<Contact>

    fun observeContact(customerId: Long): LiveData<Result<Contact>>

    suspend fun getContactsLike(text: String): List<Contact>

    suspend fun insert(contact: Contact): Long

    suspend fun delete(vararg contact: Contact): Int

    suspend fun deleteById(contactId: Long): Int

    suspend fun update(contact: Contact): Int

    suspend fun updateToBeDeleted(contactId: Long)

    suspend fun updateScheduledTreatmentsWithNewContact(oldContactId: Long, newContactId: Long)
}
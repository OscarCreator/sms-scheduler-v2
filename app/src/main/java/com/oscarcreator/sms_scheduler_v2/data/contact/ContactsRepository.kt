package com.oscarcreator.sms_scheduler_v2.data.contact

import androidx.lifecycle.LiveData
import com.oscarcreator.sms_scheduler_v2.data.Result

interface ContactsRepository {

    fun observeContacts(): LiveData<List<Contact>>

    suspend fun getCustomerById(customerId: Long): Result<Contact>

    fun observeCustomer(customerId: Long): LiveData<Result<Contact>>

    suspend fun getCustomersLike(text: String): List<Contact>

    suspend fun insert(contact: Contact): Long

    suspend fun delete(vararg contact: Contact): Int

    suspend fun deleteById(contactId: Long): Int

    suspend fun update(contact: Contact): Int
}
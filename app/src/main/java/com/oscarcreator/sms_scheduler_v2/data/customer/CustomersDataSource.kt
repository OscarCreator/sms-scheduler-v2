package com.oscarcreator.sms_scheduler_v2.data.customer

import androidx.lifecycle.LiveData
import com.oscarcreator.sms_scheduler_v2.data.Result

interface CustomersDataSource {

    fun getCustomers(): LiveData<List<Customer>>

    suspend fun getCustomer(customerId: Long): Result<Customer>

    fun observeCustomer(customerId: Long): LiveData<Result<Customer>>

    suspend fun getCustomersLike(text: String): List<Customer>

    suspend fun insert(customer: Customer): Long

    suspend fun delete(vararg customer: Customer): Int

    suspend fun deleteById(contactId: Long): Int

    suspend fun update(customer: Customer): Int
}
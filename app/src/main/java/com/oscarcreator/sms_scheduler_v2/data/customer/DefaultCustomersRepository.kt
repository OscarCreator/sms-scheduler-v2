package com.oscarcreator.sms_scheduler_v2.data.customer

import androidx.lifecycle.LiveData
import com.oscarcreator.sms_scheduler_v2.data.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DefaultCustomersRepository(
    private val customersDataSource: CustomersDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): CustomersRepository {

    override fun observeContacts(): LiveData<List<Customer>> = customersDataSource.observeContacts()

    override suspend fun getCustomerById(id: Long): Result<Customer> = customersDataSource.getCustomer(id)

    override fun observeCustomer(customerId: Long): LiveData<Result<Customer>> = customersDataSource.observeCustomer(customerId)

    override suspend fun getCustomersLike(text: String): List<Customer> =
        customersDataSource.getCustomersLike(text)

    override suspend fun insert(customer: Customer): Long = customersDataSource.insert(customer)

    override suspend fun delete(vararg customer: Customer): Int = customersDataSource.delete(*customer)

    override suspend fun deleteById(contactId: Long): Int = customersDataSource.deleteById(contactId)

    override suspend fun update(customer: Customer): Int = customersDataSource.update(customer)
}
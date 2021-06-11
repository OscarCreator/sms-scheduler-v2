package com.oscarcreator.sms_scheduler_v2.data.customer

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultCustomersRepository(
    private val customersDataSource: CustomersDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): CustomersRepository {

    override fun getCustomers(): LiveData<List<Customer>> = customersDataSource.getCustomers()

    override suspend fun getCustomer(id: Long): Customer = withContext(ioDispatcher) {
        customersDataSource.getCustomer(id)
    }

    override suspend fun getCustomersLike(text: String): List<Customer> =
        customersDataSource.getCustomersLike(text)

    override suspend fun insert(customer: Customer): Long = customersDataSource.insert(customer)

    override suspend fun delete(vararg customer: Customer): Int = customersDataSource.delete(*customer)

    override suspend fun update(customer: Customer): Int = customersDataSource.update(customer)
}
package com.oscarcreator.sms_scheduler_v2.data.customer.local

import androidx.lifecycle.LiveData
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.data.customer.CustomersDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CustomersLocalDataSource internal constructor(
    private val customerDao: CustomerDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): CustomersDataSource {

    override fun getCustomers(): LiveData<List<Customer>> = customerDao.getCustomers()

    override suspend fun getCustomer(customerId: Long): Customer = withContext(ioDispatcher) {
        customerDao.getCustomer(customerId)
    }

    override suspend fun getCustomersLike(text: String): List<Customer> = withContext(ioDispatcher) {
        customerDao.getCustomersLike("%$text%")
    }


    override suspend fun insert(customer: Customer): Long = customerDao.insert(customer)

    override suspend fun delete(vararg customer: Customer): Int = customerDao.delete(*customer)

    override suspend fun update(customer: Customer): Int = customerDao.update(customer)

}
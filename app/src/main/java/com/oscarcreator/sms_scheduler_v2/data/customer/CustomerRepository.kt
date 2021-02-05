package com.oscarcreator.sms_scheduler_v2.data.customer

class CustomerRepository private constructor(private val customerDao: CustomerDao){

    fun getCustomers() = customerDao.getCustomers()

    fun getCustomer(customerId: Long) = customerDao.getCustomer(customerId)

    suspend fun insert(customer: Customer) = customerDao.insert(customer)
    suspend fun getCustomersLike(text: String) = customerDao.getCustomersLike("%$text%")

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: CustomerRepository? = null

        fun getInstance(
            customerDao: CustomerDao
        ) = instance ?: synchronized(this) {
            instance ?: CustomerRepository(customerDao)
                .also { instance = it }
        }
    }

}
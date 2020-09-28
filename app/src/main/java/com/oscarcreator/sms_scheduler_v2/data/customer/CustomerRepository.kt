package com.oscarcreator.sms_scheduler_v2.data.customer

class CustomerRepository private constructor(private val customerDao: CustomerDao){

    fun getCustomers() = customerDao.getCustomers()

    fun getCustomer(customerId: Long) = customerDao.getCustomer(customerId)

    suspend fun insert(customer: Customer) = customerDao.insert(customer)

}
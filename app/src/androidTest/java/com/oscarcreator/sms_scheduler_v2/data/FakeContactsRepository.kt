package com.oscarcreator.sms_scheduler_v2.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.data.customer.CustomersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class FakeContactsRepository : CustomersRepository {

    var contactsServiceData: LinkedHashMap<Long, Customer> = LinkedHashMap()

    private val observableContacts = MutableLiveData<List<Customer>>()

    override fun observeContacts(): LiveData<List<Customer>> {
        runBlocking { refreshTreatments() }
        return observableContacts
    }

    override suspend fun getCustomerById(customerId: Long): Result<Customer> {
        val contact = contactsServiceData[customerId]
        return if (contact == null) {
            Result.Error(Exception("Did not find contact"))
        } else {
            Result.Success(contact)
        }
    }

    override fun observeCustomer(customerId: Long): LiveData<Result<Customer>> {
        runBlocking { refreshTreatments() }
        return observableContacts.map { customers ->
            val customer = customers.find { it.id == customerId }
            if (customer == null) {
                Result.Error(Exception("Did not find contact"))
            } else {
                Result.Success(customer)
            }
        }
    }

    override suspend fun getCustomersLike(text: String): List<Customer> {
        return contactsServiceData
            .map { it.value }
            .filter { it.name.contains(text) }
            .toList()
    }

    override suspend fun insert(customer: Customer): Long {
        contactsServiceData[customer.id] = customer
        withContext(Dispatchers.Main) {
            refreshTreatments()
        }
        return customer.id
    }

    override suspend fun delete(vararg customer: Customer): Int {
        for (value in customer) {
            contactsServiceData.remove(value.id)
        }
        refreshTreatments()
        return customer.size
    }

    override suspend fun deleteById(contactId: Long): Int {
        contactsServiceData.remove(contactId)
        refreshTreatments()
        return 1
    }

    override suspend fun update(customer: Customer): Int {
        contactsServiceData[customer.id] = customer
        refreshTreatments()
        return 1
    }

    private fun refreshTreatments() {
        observableContacts.value = contactsServiceData.values.toList()
    }
}
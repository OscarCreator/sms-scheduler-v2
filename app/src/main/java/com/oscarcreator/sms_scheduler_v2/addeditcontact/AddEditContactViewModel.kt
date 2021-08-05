package com.oscarcreator.sms_scheduler_v2.addeditcontact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.Result
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.data.customer.CustomersRepository
import com.oscarcreator.sms_scheduler_v2.util.Event
import kotlinx.coroutines.launch

class AddEditContactViewModel(
    private val contactsRepository: CustomersRepository
): ViewModel() {

    companion object {
        private const val TAG = "AddEditContactViewModel"
    }

    val name = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val money = MutableLiveData<String>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _contactUpdatedEvent = MutableLiveData<Event<Unit>>()
    val contactUpdatedEvent: LiveData<Event<Unit>> = _contactUpdatedEvent

    private var contactId: Long = -1L
    private var isNewContact = false
    private var isDataLoaded = false

    fun start(contactId: Long = -1L) {
        if (contactId == -1L) {
            isNewContact = true
            return
        }
        if (isDataLoaded) {
            //Already initialized
            return
        }

        this.contactId = contactId
        isNewContact = false
        _dataLoading.value = true

        viewModelScope.launch {
            contactsRepository.getCustomerById(contactId).let {
                if (it is Result.Success) {
                    onContactLoaded(it.data)
                } else {
                    onDataNotAvailable()
                }
            }
        }
    }

    fun saveContact() {
        val currentName = name.value
        val currentPhoneNumber = phoneNumber.value
        val currentMoney = money.value

        if (currentName == null || currentName.isEmpty() ||
            currentPhoneNumber == null || currentPhoneNumber.isEmpty() ||
            currentMoney == null || currentMoney.isEmpty()) {
            _snackbarText.value = Event(R.string.empty_contact_message)
            return
        }

        val currentContactId = contactId
        if (isNewContact || currentContactId == -1L) {
            createContact(Customer(name = currentName, phoneNumber = currentPhoneNumber, money = currentMoney.toInt()))
        } else {
            updateContact(Customer(currentContactId, currentName, currentPhoneNumber, currentMoney.toInt()))
        }
    }

    private fun onContactLoaded(contact: Customer) {
        name.value = contact.name
        phoneNumber.value = contact.phoneNumber
        money.value = contact.money.toString()
        _dataLoading.value = false
    }

    private fun onDataNotAvailable() {
        _dataLoading.value = false
    }

    private fun createContact(newCustomer: Customer) = viewModelScope.launch {
        contactsRepository.insert(newCustomer)
        _contactUpdatedEvent.value = Event(Unit)
    }

    private fun updateContact(customer: Customer) = viewModelScope.launch {
        contactsRepository.update(customer)
        _contactUpdatedEvent.value = Event(Unit)
    }

}
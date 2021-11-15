package com.oscarcreator.sms_scheduler_v2.addeditcontact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.Result
import com.oscarcreator.sms_scheduler_v2.data.contact.Contact
import com.oscarcreator.sms_scheduler_v2.data.contact.ContactsRepository
import com.oscarcreator.sms_scheduler_v2.util.Event
import kotlinx.coroutines.launch

class AddEditContactViewModel(
    private val contactsRepository: ContactsRepository
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

    private val _contactUpdatedEvent = MutableLiveData<Event<Long>>()
    val contactUpdatedEvent: LiveData<Event<Long>> = _contactUpdatedEvent

    private var contactId: Long = -1L
    private var contactGroupId: String = ""
    private var contactVersion: Long = -1L
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
            contactsRepository.getContact(contactId).let {
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
        val currentContactGroupId = contactGroupId
        if (isNewContact || currentContactId == -1L) {
            createContact(Contact(name = currentName, phoneNumber = currentPhoneNumber, money = currentMoney.toInt()))
        } else {
            updateContact(Contact(
                currentName,
                currentPhoneNumber,
                currentMoney.toInt(),
                contactGroupId = currentContactGroupId,
                contactVersion = contactVersion + 1
            ))
        }
    }

    private fun onContactLoaded(contact: Contact) {
        name.value = contact.name
        phoneNumber.value = contact.phoneNumber
        money.value = contact.money.toString()
        contactGroupId = contact.contactGroupId
        contactVersion = contact.contactVersion
        _dataLoading.value = false
    }

    private fun onDataNotAvailable() {
        _dataLoading.value = false
    }

    private fun createContact(newContact: Contact) = viewModelScope.launch {
        val newContactId = contactsRepository.insert(newContact)
        _snackbarText.value = Event(R.string.contact_saved)
        _contactUpdatedEvent.value = Event(-1)
    }

    private fun updateContact(contact: Contact) = viewModelScope.launch {

        //TODO only upcoming, update()
        // else mark as "deleted" and insert() and updateST()

        try {
            contactsRepository.deleteById(contactId)

        } catch (e: Exception) {
            contactsRepository.updateToBeDeleted(contactId)
        }

        val newContactId = contactsRepository.insert(contact)
        contactsRepository.updateScheduledTreatmentsWithNewContact(contactId, newContactId)
        _snackbarText.value = Event(R.string.contact_updated)
        _contactUpdatedEvent.value = Event(newContactId)

    }

}
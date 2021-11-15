package com.oscarcreator.sms_scheduler_v2.contactdetail

import androidx.lifecycle.*
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.Result
import com.oscarcreator.sms_scheduler_v2.data.contact.Contact
import com.oscarcreator.sms_scheduler_v2.data.contact.ContactsRepository
import com.oscarcreator.sms_scheduler_v2.util.Event
import kotlinx.coroutines.launch

class DetailContactViewModel(
    private val contactsRepository: ContactsRepository
): ViewModel() {

    private val _contactId = MutableLiveData<Long>()

    private val _contact = _contactId.switchMap { contactId ->
        contactsRepository.observeContact(contactId)
            .map { computeResult(it) }
    }
    val contact: LiveData<Contact?> = _contact

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _editContactEvent = MutableLiveData<Event<Unit>>()
    val editContactEvent: LiveData<Event<Unit>> = _editContactEvent

    private val _deleteContactEvent = MutableLiveData<Event<Unit>>()
    val deleteContactEvent: LiveData<Event<Unit>> = _deleteContactEvent

    fun start(contactId: Long) {
        if (_dataLoading.value == true || contactId == _contactId.value) {
            // already loaded
            return
        }

        _contactId.value = contactId
    }

    fun deleteContact() = viewModelScope.launch {
        _contactId.value?.let {
            try {
                contactsRepository.deleteById(it)
            } catch (e: Exception) {
                contactsRepository.updateToBeDeleted(it)
            }
            _snackbarText.value = Event(R.string.contact_deleted)
            _deleteContactEvent.value = Event(Unit)
        }
    }

    fun editContact() {
        _editContactEvent.value = Event(Unit)
    }

    private fun computeResult(contactResult: Result<Contact>): Contact? {
        return if (contactResult is Result.Success) {
            contactResult.data
        } else {
            _snackbarText.value = Event(R.string.loading_contact_error)
            null
        }
    }

}
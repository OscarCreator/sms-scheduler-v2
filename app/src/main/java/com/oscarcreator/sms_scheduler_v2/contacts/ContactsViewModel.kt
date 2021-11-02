package com.oscarcreator.sms_scheduler_v2.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oscarcreator.sms_scheduler_v2.data.contact.Contact
import com.oscarcreator.sms_scheduler_v2.data.contact.ContactsRepository
import com.oscarcreator.sms_scheduler_v2.util.Event

class ContactsViewModel(
    private val contactsRepository: ContactsRepository
) : ViewModel() {

    val contacts: LiveData<List<Contact>> = contactsRepository.observeContacts()


    private val _newContactEvent = MutableLiveData<Event<Unit>>()
    val newContactEvent: LiveData<Event<Unit>> = _newContactEvent

    private val _openContactEvent = MutableLiveData<Event<Long>>()
    val openContactEvent: LiveData<Event<Long>> = _openContactEvent

    /**
     * Is called when add contact FAB is clicked.
     * */
    fun addNewContact(){
        _newContactEvent.value = Event(Unit)
    }

    /**
     * Is called when contact item in adapter is clicked.
     * */
    fun openContact(messageId: Long){
        _openContactEvent.value = Event(messageId)
    }

}
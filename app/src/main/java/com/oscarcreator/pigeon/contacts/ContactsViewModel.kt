package com.oscarcreator.pigeon.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.oscarcreator.pigeon.data.contact.Contact
import com.oscarcreator.pigeon.data.contact.ContactsRepository
import com.oscarcreator.pigeon.util.Event

class ContactsViewModel(
    private val contactsRepository: ContactsRepository
) : ViewModel() {

    val contacts: LiveData<List<Contact>> = contactsRepository.observeContacts()


    private val _newContactEvent = MutableLiveData<Event<Unit>>()
    val newContactEvent: LiveData<Event<Unit>> = _newContactEvent

    private val _openContactEvent = MutableLiveData<Event<Long>>()
    val openContactEvent: LiveData<Event<Long>> = _openContactEvent

    val searchText = MutableLiveData("")

    fun getContactsLike(textLike: String): LiveData<List<Contact>> {
        return contacts.map {
            it.filter { contact ->
                contact.name.contains(textLike, true)
            }
        }
    }

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
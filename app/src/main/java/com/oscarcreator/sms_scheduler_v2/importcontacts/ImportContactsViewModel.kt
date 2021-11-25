package com.oscarcreator.sms_scheduler_v2.importcontacts

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oscarcreator.sms_scheduler_v2.data.contact.Contact
import com.oscarcreator.sms_scheduler_v2.data.contact.ContactsRepository
import com.oscarcreator.sms_scheduler_v2.util.Event
import kotlinx.coroutines.launch

class ImportContactsViewModel(
    val contactsRepository: ContactsRepository
): ViewModel() {

    private val _savedContactsEvent = MutableLiveData<Event<Unit>>()
    val savedContactsEvent: LiveData<Event<Unit>> = _savedContactsEvent

    val searchText = MutableLiveData<String>()

    var items = mutableStateListOf<ImportContact>()

    fun selectItem(index: Int) {
        val item = items[index]
        items[index] = item.copy(selected = !item.selected)
    }

    fun setItems(list: List<ImportContact>) {
        viewModelScope.launch {
            val contacts = contactsRepository.getContacts()
            for (contact in list) {
                val foundContact = contacts.find { it.phoneNumber == contact.phoneNumber }
                if (foundContact != null) {
                    items.add(contact.copy(importedName = foundContact.name, selected = true))
                } else {
                    items.add(contact)
                }
            }
        }
    }

    fun save() {
        val saveContacts = items
            .filter { it.importedName == null && it.selected }
            .map { Contact(it.name, it.phoneNumber) }
        viewModelScope.launch {
            for (contact in saveContacts) {
                contactsRepository.insert(contact)
            }
            _savedContactsEvent.value = Event(Unit)
        }
    }

    fun getContactsLike(textLike: String): SnapshotStateList<ImportContact> {
        return items.filter { it.name.contains(textLike, true) }.toMutableStateList()
    }

    fun selectAll(selected: Boolean, searchText: String?) {

        val allSelected = items.map {
            if (it.name.contains(searchText ?: "", true)) {
                ImportContact(it.id, it.name, it.phoneNumber, if (it.importedName != null) true else selected, it.importedName)
            } else {
                it
            }
        }
        items.clear()
        items.addAll(allSelected)
    }

}
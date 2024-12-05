package com.oscarcreator.pigeon.contact


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.oscarcreator.pigeon.FakeContactsRepository
import com.oscarcreator.pigeon.MainCoroutineRule
import com.oscarcreator.pigeon.addeditcontact.AddEditContactViewModel
import com.oscarcreator.pigeon.data.contact.Contact
import com.oscarcreator.pigeon.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddEditContactViewModelTest {

    private lateinit var addEditContactViewModel: AddEditContactViewModel

    private lateinit var contactsRepository: FakeContactsRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        contactsRepository = FakeContactsRepository()

        addEditContactViewModel = AddEditContactViewModel(contactsRepository)
    }

    @Test
    fun saveContactToRepository_contactSaved() {
        val contact = Contact( "Bengt", "0238492", 200)

        addEditContactViewModel.start()

        addEditContactViewModel.apply {
            name.value = contact.name
            phoneNumber.value = contact.phoneNumber
            money.value = contact.money.toString()
        }

        addEditContactViewModel.saveContact()

        val newContact = contactsRepository.contactsServiceData.values.first()

        assertThat(newContact.name, `is`(contact.name))
        assertThat(newContact.phoneNumber, `is`(contact.phoneNumber))
        assertThat(newContact.money, `is`(contact.money))
    }

    @Test
    fun loadContact_dataShown() {
        val contact = Contact( "Bengt", "0238492", 200)

        runBlocking {
            contactsRepository.insert(contact)
        }

        addEditContactViewModel.start(contact.contactId)

        assertThat(addEditContactViewModel.name.getOrAwaitValue(), `is`(contact.name))
        assertThat(addEditContactViewModel.phoneNumber.getOrAwaitValue(), `is`(contact.phoneNumber))
        assertThat(addEditContactViewModel.money.getOrAwaitValue(), `is`(contact.money.toString()))
    }

    @Test
    fun saveContact_emptyName_error() {
        saveContactAndAssert("", "030303", "400", false)
    }

    @Test
    fun saveContact_nullName_error() {
        saveContactAndAssert(null, "020202", "400", false)
    }

    @Test
    fun saveContact_emptyPhoneNumber_error() {
        saveContactAndAssert("Bengt", "", "400", false)
    }

    @Test
    fun saveContact_nullPhoneNumber_error() {
        saveContactAndAssert("Bengt", null, "400", false)
    }

    @Test
    fun saveContact_emptyMoney_error() {
        saveContactAndAssert("Bengt", "020202", "", false)
    }

    @Test
    fun saveContact_nullMoney_error() {
        saveContactAndAssert("Bengt", "020202", null, false)
    }

    @Test
    fun saveContact_validContact_valid() {
        saveContactAndAssert("Bengt", "020202", "4042", true)
    }

    private fun saveContactAndAssert(name: String?, phoneNumber: String?, money: String?, expectedIsSaved: Boolean) {
        addEditContactViewModel.start()
        addEditContactViewModel.apply {
            this.name.value = name
            this.phoneNumber.value = phoneNumber
            this.money.value = money
        }

        addEditContactViewModel.saveContact()

        assertThat(contactsRepository.contactsServiceData.values.isNotEmpty(), `is`(expectedIsSaved))

    }
}
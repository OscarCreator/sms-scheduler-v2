package com.oscarcreator.sms_scheduler_v2.contact

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oscarcreator.sms_scheduler_v2.MainActivity
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.FakeContactsRepository
import com.oscarcreator.sms_scheduler_v2.data.contact.Contact
import com.oscarcreator.sms_scheduler_v2.util.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ContactFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var repository: FakeContactsRepository

    @Before
    fun initRepository() {
        repository = FakeContactsRepository()
        ServiceLocator.contactsRepository = repository
    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepositories()
    }

    @Test
    fun displayContact_whenRepositoryHasData() {
        val contact = Contact("Bengt", "020304020")
        runBlocking {
            repository.insert(contact)
        }

        val activityScenario = launchActivity()
        composeTestRule.onNodeWithText(contact.name)
            .assertIsDisplayed()

        activityScenario.close()
    }

    @Test
    fun deleteContact() {
        val contact = Contact( "Bengt", "02002034")
        runBlocking {
            repository.insert(contact)
        }

        val activityScenario = launchActivity()

        composeTestRule.onNodeWithText(contact.name)
            .assertIsDisplayed()
            .performClick()

        onView(withId(R.id.delete))
            .perform(click())


        assertThat(repository.contactsServiceData.size, `is`(0))
        // TODO could be check in UI with idling resource
//        composeTestRule.onNodeWithText(contact.name)
//            .assertDoesNotExist()

        activityScenario.close()
    }

    @Test
    fun addNewContact_isClicked_navigatesToAddEditFragment_andBack() {
        val activityScenario = launchActivity()

        composeTestRule.onNodeWithContentDescription("Add new contact")
            .assertIsDisplayed()
            .performClick()

        activityScenario.onActivity {
            assertThat(it.findNavController(R.id.nav_host_fragment).currentDestination?.id, `is`(R.id.addEditContactFragment))
        }

        pressBack()

        activityScenario.onActivity {
            assertThat(it.findNavController(R.id.nav_host_fragment).currentDestination?.id, `is`(R.id.contactListFragment))
        }

        activityScenario.close()
    }

    @Test
    fun validContact_isSaved_andNavigatesBack() {
        val contact = Contact( "Bengt", "03498239", 10)

        val activityScenario = launchActivity()

        composeTestRule.onNodeWithContentDescription("Add new contact")
            .assertIsDisplayed()
            .performClick()

        onView(withId(R.id.tv_name))
            .check(matches(isDisplayed()))
            .perform(typeText(contact.name))

        onView(withId(R.id.tv_phone_num))
            .check(matches(isDisplayed()))
            .perform(typeText(contact.phoneNumber))

        // Not used yet
        //onView(withId(R.id.et_money))
        //    .check(matches(isDisplayed()))
        //    .perform(typeText(contact.money.toString()))
        //    .perform(pressImeActionButton())
        Espresso.closeSoftKeyboard()

        onView(withId(R.id.fab_save_contact))
            .check(matches(isDisplayed()))
            .perform(click())

        assertThat(repository.contactsServiceData.filter {
            it.value.name == contact.name &&
            it.value.phoneNumber == contact.phoneNumber
        }.size, `is`(1))

        activityScenario.onActivity {
            assertThat(it.findNavController(R.id.nav_host_fragment).currentDestination?.id, `is`(R.id.contactListFragment))
        }

        activityScenario.close()

    }

    @Test
    fun invalidContact_isNotSaved() {
        val contact = Contact( "Bengt", "03498239", 10)

        val activityScenario = launchActivity()

        composeTestRule.onNodeWithContentDescription("Add new contact")
            .assertIsDisplayed()
            .performClick()

        onView(withId(R.id.tv_name))
            .check(matches(isDisplayed()))
            .perform(typeText(contact.name))

        pressBack()

        //missing phone number and money

        onView(withId(R.id.fab_save_contact))
            .check(matches(isDisplayed()))
            .perform(click())

        assertThat(repository.contactsServiceData.filter {
            it.value.name == contact.name &&
                    it.value.phoneNumber == contact.phoneNumber &&
                    it.value.money == contact.money
        }.size, `is`(0))

        activityScenario.close()
    }

    private fun launchActivity(): ActivityScenario<MainActivity> {
        val activityScenario = launch(MainActivity::class.java)

        activityScenario.onActivity { activity ->
            activity.findNavController(R.id.nav_host_fragment).navigate(R.id.contactListFragment)
        }
        return activityScenario
    }
}
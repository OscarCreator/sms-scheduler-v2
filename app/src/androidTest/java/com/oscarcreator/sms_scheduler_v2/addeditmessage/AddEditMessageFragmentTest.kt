package com.oscarcreator.sms_scheduler_v2.addeditmessage

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.FakeMessagesRepository
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.message.MessagesRepository
import com.oscarcreator.sms_scheduler_v2.util.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class AddEditMessageFragmentTest {

    private lateinit var repository: MessagesRepository

    @Before
    fun initRepository() {
        repository = FakeMessagesRepository()
        ServiceLocator.messagesRepository = repository
    }

    @After
    fun cleanUpDb() = runBlockingTest {
        ServiceLocator.resetRepositories()
    }

    @Test
    fun message_displayedInUi() = runBlockingTest {
        val message = Message(1, "Some kind of message text")
        repository.insert(message)

        val bundle = AddEditMessageFragmentArgs(message.id).toBundle()
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragment(navController, bundle)

        onView(withId(R.id.et_message))
            .check(matches(
                allOf(isDisplayed(), withText(message.message))))
    }


    private fun launchFragment(navController: TestNavHostController, bundle: Bundle) {
        runOnUiThread {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.addEditMessageFragment)
        }
        val scenario = launchFragmentInContainer<AddEditMessageFragment>(bundle, R.style.AppTheme)

        scenario.onFragment {
            Navigation.setViewNavController(it.requireView(), navController)
        }
    }

}
package com.oscarcreator.pigeon.message

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.oscarcreator.pigeon.MainActivity
import com.oscarcreator.pigeon.R
import com.oscarcreator.pigeon.data.FakeMessagesRepository
import com.oscarcreator.pigeon.data.message.Message
import com.oscarcreator.pigeon.messages.MessagesFragment
import com.oscarcreator.pigeon.util.ServiceLocator
import com.oscarcreator.pigeon.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test for Messages screen.
 * */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class MessagesFragmentTest {

    private lateinit var repository: FakeMessagesRepository

    @Before
    fun initRepository(){
        repository = FakeMessagesRepository()
        ServiceLocator.messagesRepository = repository
    }

    @After
    fun cleanUpDb() = runTest {
        ServiceLocator.resetRepositories()
    }

    @Test
    fun messageItem_isClicked_returnsToAddEditScheduledTreatment() = runTest {
        val message = Message( "Test string", messageId = 1)
        repository.insert(message)

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragment(navController)

        onView(ViewMatchers.withText(message.message))
            .perform(ViewActions.click())

        assertThat(navController.currentDestination?.id, `is`(R.id.messageDetailFragment))
    }

    // TODO launch activity instead
    //  because action bar is not displayed by the fragment
    @Test
    fun messageItem_isLongPressed_showsActionMenu_And_returnsWhenXisClicked() = runTest {
        val message = Message( "Test string", messageId = 1)
        repository.insert(message)

        // navigate to messages
        ActivityScenario.launch(MainActivity::class.java)
        onView(ViewMatchers.withId(R.id.fab_add_treatment)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_message))
            .perform(ViewActions.click())

        onView(ViewMatchers.withText(message.message))
            .perform(ViewActions.longClick())

        //Action mode is displayed
        onView(ViewMatchers.withResourceName("action_mode_close_button"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        //Close action mode
            .perform(ViewActions.click())
        //Action mode is not displayed
        onView(ViewMatchers.withResourceName("action_mode_close_button"))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))

    }

    //This is an E2E test because it depends on the activity
    @Test
    fun addButton_isClicked_showsAddEditMessage() = runTest {

        // navigate to messages
        ActivityScenario.launch(MainActivity::class.java)
        onView(ViewMatchers.withId(R.id.fab_add_treatment)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_message))
            .perform(ViewActions.click())

        onView(ViewMatchers.withId(R.id.fab_add_message))
            .perform(ViewActions.click())

        val actualMessage = Message(message = "Some text 2567")

        onView(ViewMatchers.withId(R.id.et_message))
            .perform(ViewActions.typeText(actualMessage.message))

        Espresso.closeSoftKeyboard()

        onView(ViewMatchers.withId(R.id.fab_save_message))
            .perform(ViewActions.click())

        onView(ViewMatchers.withText(actualMessage.message))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        runOnUiThread {
            assertThat(repository.observeAllMessages().getOrAwaitValue()[0].message, `is`(actualMessage.message))
        }
    }

    @Test
    fun actionMode_messageItem_isSelected_deleteButtonClicked_removesSelectedMessage() = runTest {
        val message = Message( "Test string", messageId = 1)
        repository.insert(message)

        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        launchFragment(navController)

        onView(ViewMatchers.withText(message.message))
            .perform(ViewActions.longClick())

        onView(ViewMatchers.withId(R.id.delete))
            .perform(ViewActions.click())

        //message is deleted

        assertThat(repository.messagesServiceData.size, `is`(0))
        //and not displayed
        onView(ViewMatchers.withId(R.id.rv_message_list))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(0)))
    }

    private fun launchFragment(navController: TestNavHostController){
        runOnUiThread {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.messageListFragment)
        }
        val scenario = launchFragmentInContainer<MessagesFragment>(Bundle(), R.style.AppTheme)

        scenario.onFragment{
            Navigation.setViewNavController(it.requireView(), navController)
        }
    }


}
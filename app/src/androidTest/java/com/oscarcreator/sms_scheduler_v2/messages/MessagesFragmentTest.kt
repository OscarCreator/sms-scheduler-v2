package com.oscarcreator.sms_scheduler_v2.messages

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.FakeMessagesRepository
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.message.MessagesRepository
import com.oscarcreator.sms_scheduler_v2.util.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
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

    private lateinit var repository: MessagesRepository

    @Before
    fun initRepository(){
        repository = FakeMessagesRepository()
        ServiceLocator.messagesRepository = repository
    }

    @After
    fun cleanUpDb() = runBlockingTest {
        ServiceLocator.resetRepositories()
    }

    @Test
    fun messageItem_isClicked_returnsToAddEditScheduledTreatment() = runBlockingTest {
        val message = Message(1, "Test string")
        repository.insert(message)

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragment(navController)

        Espresso.onView(ViewMatchers.withText(message.message))
            .perform(ViewActions.click())

        assertThat(navController.currentDestination?.id, `is`(R.id.messageDetailFragment))
    }

    @Test
    fun messageItem_isLongPressed_showsActionMenu_And_returnsWhenXisClicked() = runBlockingTest {
        val message = Message(3, "Lorry is my lord")
        repository.insert(message)

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragment(navController)

        Espresso.onView(ViewMatchers.withText(message.message))
            .perform(ViewActions.longClick())

        //Action mode is displayed
        Espresso.onView(ViewMatchers.withResourceName("action_mode_bar"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        //Close action mode
        Espresso.onView(ViewMatchers.withResourceName("action_mode_close_button"))
            .perform(ViewActions.click())
        //Action mode is not displayed
        Espresso.onView(ViewMatchers.withResourceName("action_mode_bar"))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))

    }

    //This is an E2E test because it depends on the activity
//    @Test
//    fun addButton_isClicked_showsAddEditMessage() = runBlockingTest {
//
//        val navController = TestNavHostController(
//            ApplicationProvider.getApplicationContext())
//        launchFragment(navController)
//
//        Espresso.onView(ViewMatchers.withId(R.id.new_message))
//            .perform(ViewActions.click())
//
//        assertThat(navController.currentDestination?.id, `is`(R.id.addEditMessageFragment))
//
//        val actualMessage = Message(message = "Some text 2567")
//
//        Espresso.onView(ViewMatchers.withId(R.id.et_message))
//            .perform(ViewActions.typeText(actualMessage.message))
//
//        Espresso.onView(ViewMatchers.withId(R.id.complete))
//            .perform(ViewActions.click())
//
//        Espresso.onView(ViewMatchers.withText(actualMessage.message))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//
//        assertThat(repository.getMessages().value?.get(0)?.message, `is`(actualMessage.message))
//
//    }

    @Test
    fun actionMode_messageItem_isSelected_deleteButtonClicked_removesSelectedMessage() = runBlockingTest {
        val message = Message(5, "3tgret43ewretg wrewfretg rtdfe")
        repository.insert(message)

        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        launchFragment(navController)

        Espresso.onView(ViewMatchers.withText(message.message))
            .perform(ViewActions.longClick())

        Espresso.onView(ViewMatchers.withId(R.id.delete))
            .perform(ViewActions.click())

        //message is deleted
        assertThat(repository.observeMessages().value?.size, `is`(0))
        //and not displayed
        Espresso.onView(ViewMatchers.withId(R.id.rv_message_list))
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
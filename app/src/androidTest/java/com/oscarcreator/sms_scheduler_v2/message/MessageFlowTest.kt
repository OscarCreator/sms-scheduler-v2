package com.oscarcreator.sms_scheduler_v2.message

import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oscarcreator.sms_scheduler_v2.MainActivity
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.FakeMessagesRepository
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.message.MessagesRepository
import com.oscarcreator.sms_scheduler_v2.messages.MessageAdapter
import com.oscarcreator.sms_scheduler_v2.util.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


/**
 * End-to-End test for messages
 * */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class MessageFlowTest {

    private lateinit var repository: FakeMessagesRepository

    @Before
    fun setupRepository() {
        repository = FakeMessagesRepository()
        ServiceLocator.messagesRepository = repository
    }

    @After
    fun reset() {
        ServiceLocator.resetRepositories()
    }

    //TODO idling resource instead of delay. Idling resource currently not working
    // implemented from https://youtu.be/_96FT7E6PL4

    @Test
    fun addMessage() {
        val activityScenario = launchActivity()

        val messageText = "This is a sample message."

        //navigate to add/edit fragment
        onView(withId(R.id.fab_add_message)).perform(click())
        //write the data
        onView(withId(R.id.et_message))
            .perform(typeText(messageText))

        Espresso.closeSoftKeyboard()

        //save the message
        onView(withId(R.id.fab_save_message)).perform(click())

        //check so the item is displayed in the list
        onView(withText(messageText))
            .check(matches(isDisplayed()))
        //check so the item is inserted

        assertThat(repository.messagesServiceData.size, `is`(1))
        assertThat(repository.messagesServiceData.values.first().message, `is`(messageText))

        //check snackbar
        activityScenario.close()
    }

    @Test
    fun editMessage() {
        val activityScenario = launchActivity()

        val message = Message( message = "Some crazy text.", messageId = 5)
        repository.insertBlocking(message)

        onView(withId(R.id.rv_message_list))
            .perform(RecyclerViewActions
                .actionOnItemAtPosition<MessageAdapter.MessageListViewHolder>(0, click()))

        //navigate to edit fragment
        onView(withId(R.id.edit)).perform(click())
        //replace text with new
        val newText = "A new text for the message."
        onView(withId(R.id.et_message))
            .perform(replaceText(newText))
        //save the new message
        onView(withId(R.id.fab_save_message)).perform(click())

        onView(withText(newText)).check(matches(isDisplayed()))
        assertThat(repository.messagesServiceData.values.find { it.message == newText }?.message, `is`(newText))

        activityScenario.close()
    }


    @Test
    fun deleteMessage() {
        val activityScenario = launchActivity()

        val message = Message(message = "Some crazy text.")

        repository.insertBlocking(message)


        onView(withText(message.message)).perform(longClick())

        //click on delete to delete the selected message
        onView(withId(R.id.delete)).perform(click())

        assertThat(repository.messagesServiceData.size, `is`(0))

        activityScenario.close()
    }

    @Test
    fun deleteMultipleMessages() {
        val activityScenario = launchActivity()

        val message1 = Message(message = "Message 1", messageId = 1)
        val message2 = Message(message = "Message 2", messageId = 2)
        repository.insertBlocking(message1)
        repository.insertBlocking(message2)

        onView(withText(message1.message)).perform(longClick())
        onView(withText(message2.message)).perform(click())
        onView(withId(R.id.delete)).perform(click())

        assertThat(repository.messagesServiceData.size, `is`(0))

        activityScenario.close()
    }

    @Test
    fun viewMessage() {
        val activityScenario = launchActivity()

        val message = Message(message = "Text here")
        repository.insertBlocking(message)
        runBlocking { delay(100) }
        onView(withId(R.id.rv_message_list))
            .perform(RecyclerViewActions
                .actionOnItemAtPosition<MessageAdapter.MessageListViewHolder>(0, click()))

        onView(withId(R.id.tv_message)).check(matches(withText(message.message)))

        activityScenario.close()
    }

    @Test
    fun cancelDeleteMessage() {
        val activityScenario = launchActivity()

        val message = Message(message = "Message here")
        repository.insertBlocking(message)
        runBlocking { delay(100) }
        onView(withId(R.id.rv_message_list))
            .perform(RecyclerViewActions
                .actionOnItemAtPosition<MessageAdapter.MessageListViewHolder>(0, longClick()))

        onView(withResourceName("action_mode_close_button")).perform(click())

        assertThat(repository.messagesServiceData.size, `is`(1))

        activityScenario.close()
    }

    private fun MessagesRepository.insertBlocking(message: Message) = runBlocking {
        insert(message)
    }

    private fun launchActivity(): ActivityScenario<MainActivity> {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        activityScenario.onActivity { activity ->
            activity.findNavController(R.id.nav_host_fragment).navigate(R.id.messageListFragment)
        }
        return activityScenario
    }
}
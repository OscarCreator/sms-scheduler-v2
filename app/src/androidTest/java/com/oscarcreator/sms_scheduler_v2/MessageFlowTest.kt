package com.oscarcreator.sms_scheduler_v2

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.message.MessagesRepository
import com.oscarcreator.sms_scheduler_v2.messages.MessageAdapter
import com.oscarcreator.sms_scheduler_v2.util.ServiceLocator
import com.oscarcreator.sms_scheduler_v2.util.observeOnce
import kotlinx.coroutines.*
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * TODO a different name?
 *
 * End-to-End test for messages
 * */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class MessageFlowTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var repository: MessagesRepository

    @Before
    fun init() {
        repository = ServiceLocator.provideMessagesRepository(getApplicationContext(),
            CoroutineScope(Dispatchers.IO))
        repository.removeMessageBlocking()

        navigateToMessagesFragment()
    }

    @After
    fun reset() {
        ServiceLocator.resetRepositories()
    }

    //TODO idling resource instead of delay. Idling resource currently not working
    // implemented from https://youtu.be/_96FT7E6PL4

    @Test
    fun addMessage() {

        val messageText = "This is a sample message."

        //navigate to add/edit fragment
        onView(withId(R.id.new_message)).perform(click())
        //write the data
        onView(withId(R.id.et_message)).perform(typeText(messageText))
        //save the message
        onView(withId(R.id.complete)).perform(click())

        //check so the item is displayed in the list
        onView(withText(messageText))
            .check(matches(isDisplayed()))
        //check so the item is inserted
        runBlocking(Dispatchers.Main) {
            repository.observeMessages().observeOnce {
                assertThat(it.size, `is`(1))
                assertThat(it[0].message, `is`(messageText))
            }
        }

        //check snackbar
    }

    @Test
    fun editMessage() {

        val message = Message(5, message = "Some crazy text.")
        repository.insertBlocking(message)
        runBlocking { delay(100) }
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
        onView(withId(R.id.complete)).perform(click())

        onView(withText(newText)).check(matches(isDisplayed()))
        runBlocking {
            //check so the text is changed
            assertThat(repository.getMessage(message.id).message, `is`(newText))
        }
    }


    @Test
    fun deleteMessage() {

        val message = Message(message = "Some crazy text.")

        repository.insertBlocking(message)

        runBlocking { delay(100) }
//        onView(withId(R.id.rv_message_list))
//            .perform(RecyclerViewActions
//                .actionOnItem<MessageAdapter.MessageListViewHolder>(
//                    hasDescendant(withText(message.message)), longClick()))
        onView(withText(message.message)).perform(longClick())

        //click on delete to delete the selected message
        onView(withId(R.id.delete)).perform(click())

        runBlocking(Dispatchers.Main) {
            repository.observeMessages().observeOnce {
                assertThat(it.size, `is`(0))
            }

        }
    }

    @Test
    fun deleteMultipleMessages() {
        val message1 = Message(message = "Message 1")
        val message2 = Message(message = "Message 2")
        repository.insertBlocking(message1)
        repository.insertBlocking(message2)
        runBlocking { delay(100) }
        onView(withId(R.id.rv_message_list))
            .perform(RecyclerViewActions
                .actionOnItemAtPosition<MessageAdapter.MessageListViewHolder>(0, longClick()))

        onView(withId(R.id.rv_message_list))
            .perform(RecyclerViewActions
                .actionOnItem<MessageAdapter.MessageListViewHolder>(
                    hasDescendant(withText(message2.message)), click()))

        onView(withId(R.id.delete)).perform(click())

        runBlocking(Dispatchers.Main) {

            repository.observeMessages().observeOnce {
                assertThat(it.size, `is`(0))
            }

        }

    }

    @Test
    fun viewMessage() {

        val message = Message(message = "Text here")
        repository.insertBlocking(message)
        runBlocking { delay(100) }
        onView(withId(R.id.rv_message_list))
            .perform(RecyclerViewActions
                .actionOnItemAtPosition<MessageAdapter.MessageListViewHolder>(0, click()))

        onView(withId(R.id.tv_message)).check(matches(withText(message.message)))

    }

    @Test
    fun cancelDeleteMessage() {
        val message = Message(message = "Message here")
        repository.insertBlocking(message)
        runBlocking { delay(100) }
        onView(withId(R.id.rv_message_list))
            .perform(RecyclerViewActions
                .actionOnItemAtPosition<MessageAdapter.MessageListViewHolder>(0, longClick()))

        onView(withResourceName("action_mode_close_button")).perform(click())

        runBlocking {
            withContext(Dispatchers.Main) {
                repository.observeMessages().observeOnce {
                    assertThat(it.size, `is`(1))
                }
            }
        }

    }


    private fun navigateToMessagesFragment() {
        onView(withId(R.id.fab_add_treatment)).perform(click())
        onView(withId(R.id.btn_message)).perform(click())
    }

    private fun MessagesRepository.removeMessageBlocking() {
        runBlocking(Dispatchers.Main) {
            observeMessages().observeOnce {
                for (message in it){
                    runBlocking {
                        delete(message)
                    }
                }
            }
        }
    }


    private fun MessagesRepository.insertBlocking(message: Message) = runBlocking {
        insert(message)
    }
}
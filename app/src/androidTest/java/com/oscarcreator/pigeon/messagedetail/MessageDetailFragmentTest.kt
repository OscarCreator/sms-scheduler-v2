package com.oscarcreator.pigeon.messagedetail

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.oscarcreator.pigeon.R
import com.oscarcreator.pigeon.data.FakeMessagesRepository
import com.oscarcreator.pigeon.data.message.Message
import com.oscarcreator.pigeon.data.message.MessagesRepository
import com.oscarcreator.pigeon.util.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class MessageDetailFragmentTest {

    private lateinit var repository: MessagesRepository

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
    fun message_displayedInUi() = runTest {
        val message = Message( "A Unique message", messageId = 2)
        repository.insert(message)

        val bundle = MessageDetailFragmentArgs(message.messageId).toBundle()
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragment(navController, bundle)

        onView(withId(R.id.tv_message))
            .check(matches(
                allOf(isDisplayed(), withText(message.message))))


    }

    private fun launchFragment(navController: TestNavHostController, bundle: Bundle = Bundle.EMPTY){
        UiThreadStatement.runOnUiThread {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.messageListFragment)
        }
        val scenario = launchFragmentInContainer<MessageDetailFragment>(bundle, R.style.AppTheme)

        scenario.onFragment{
            Navigation.setViewNavController(it.requireView(), navController)
        }
    }

}
package com.oscarcreator.pigeon.message

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.oscarcreator.pigeon.FakeMessagesRepository
import com.oscarcreator.pigeon.MainCoroutineRule
import com.oscarcreator.pigeon.addeditmessage.AddEditMessageViewModel
import com.oscarcreator.pigeon.data.message.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddEditMessageViewModelTest {

    private lateinit var addEditMessageViewModel: AddEditMessageViewModel

    private lateinit var repository: FakeMessagesRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        repository = FakeMessagesRepository()
        addEditMessageViewModel = AddEditMessageViewModel(repository)
    }

    @Test
    fun saveMessageToRepository_messageIsSaved() {
        addEditMessageViewModel.start()

        val messageText = "Some kind of random sameple \ntext"

        addEditMessageViewModel.apply {
            message.value = messageText
        }

        addEditMessageViewModel.saveMessage()

        val message = repository.messagesServiceData.values.first()
        assertThat(message.message, `is`(messageText))
    }

    @Test
    fun loadMessage_dataShown() {
        val message = Message("some random text", messageId = 4)

        runBlocking { repository.insert(message) }

        addEditMessageViewModel.start(message.messageId)

        assertThat(addEditMessageViewModel.message.value, `is`(message.message))
    }
}
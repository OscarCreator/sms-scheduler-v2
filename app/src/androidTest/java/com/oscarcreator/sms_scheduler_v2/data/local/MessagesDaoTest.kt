package com.oscarcreator.sms_scheduler_v2.data.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.message.local.MessagesDao
import com.oscarcreator.sms_scheduler_v2.util.observeOnce
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MessagesDaoTest : BaseDaoTest() {

    lateinit var messagesDao: MessagesDao

    @Before
    override fun inititalizeDatabase() {
        super.inititalizeDatabase()
        messagesDao = database.messageDao()

    }

    @Test
    fun message_insertedInDatabase_returnsMessage() = runBlocking {
        val message = Message(messageId = 1, message = "test message")
        assertThat(messagesDao.insert(message), `is`(1))

        messagesDao.observeMessages().observeOnce {
            assertThat(it, `is`(listOf(message)))
        }
    }

    @Test
    fun message_insertedAndDeleted_returnsEmpty() = runBlocking {
        val message = Message(messageId = 5, message = "test message2")
        assertThat(messagesDao.insert(message), `is`(5))
        assertThat(messagesDao.delete(message), `is`(1))

        messagesDao.observeMessages().observeOnce {
            assertThat(it, CoreMatchers.`is`(emptyList()))
        }
    }

    @Test
    fun message_insertedAndUpdated_returnsUpdated() = runBlocking {
        val message = Message(messageId = 100, message = "test message3")
        assertThat(messagesDao.insert(message), `is`(100))

        val updatedMessage = Message(messageId = 100, message = "test message3 updated")
        assertThat(messagesDao.update(updatedMessage), `is`(1))

        messagesDao.observeMessages().observeOnce {
            assertThat(it, `is`(listOf(updatedMessage)))
        }
    }


}
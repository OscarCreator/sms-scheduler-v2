package com.oscarcreator.sms_scheduler_v2.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.message.MessageDao
import com.oscarcreator.sms_scheduler_v2.util.observeOnce
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MessageDaoTest : BaseDaoTest() {

    lateinit var messageDao: MessageDao

    @Before
    override fun inititalizeDatabase() {
        super.inititalizeDatabase()
        messageDao = database.messageDao()

    }

    @Test
    fun message_insertedInDatabase_returnsMessage() = runBlocking {
        val message = Message(id = 1, message = "test message", isTemplate = false)
        assertThat(messageDao.insert(message), `is`(1))

        messageDao.getMessages().observeOnce {
            assertThat(it, `is`(listOf(message)))
        }
    }

    @Test
    fun message_insertedAndDeleted_returnsEmpty() = runBlocking {
        val message = Message(id = 5, message = "test message2", isTemplate = true)
        assertThat(messageDao.insert(message), `is`(5))
        assertThat(messageDao.delete(message), `is`(1))

        messageDao.getMessages().observeOnce {
            assertThat(it, CoreMatchers.`is`(emptyList()))
        }
    }

    @Test
    fun message_insertedAndUpdated_returnsUpdated() = runBlocking {
        val message = Message(id = 100, message = "test message3", isTemplate = true)
        assertThat(messageDao.insert(message), `is`(100))

        val updatedMessage = Message(id = 100, message = "test message3 updated", isTemplate = false)
        assertThat(messageDao.update(updatedMessage), `is`(1))

        messageDao.getMessages().observeOnce {
            assertThat(it, `is`(listOf(updatedMessage)))
        }
    }


}
package com.oscarcreator.pigeon.addeditscheduledtreatment

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.oscarcreator.pigeon.data.*
import com.oscarcreator.pigeon.data.contact.Contact
import com.oscarcreator.pigeon.data.message.Message
import com.oscarcreator.pigeon.data.scheduled.ScheduledTreatment
import com.oscarcreator.pigeon.data.timetemplate.TimeTemplate
import com.oscarcreator.pigeon.data.treatment.Treatment
import com.oscarcreator.pigeon.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class AddEditScheduledTreatmentViewModelTest {

    private lateinit var viewModel: AddEditScheduledTreatmentViewModel

    private val timeTemplate = TimeTemplate(1000 * 60 * 60 * 23, timeTemplateId = 6)
    private val message = Message( "some old text", false, messageId = 5)
    private val contact1 = Contact( "Bosse", "40602380", contactId = 4)
    private val contact2 = Contact("Bergit", "0720934592", 4000, contactId = 2)
    private val treatment = Treatment("Treatment 4", 400, 90, treatmentId = 9)

    private lateinit var scheduledTreatmentsRepository: FakeScheduledTreatmentsRepository
    private lateinit var contactsRepository: FakeContactsRepository
    private lateinit var treatmentsRepository: FakeTreatmentsRepository
    private lateinit var timeTemplatesRepository: FakeTimeTemplateRepository
    private lateinit var messagesRepository: FakeMessagesRepository

    @Before
    fun initializeDatabase() = runBlocking {
        scheduledTreatmentsRepository = FakeScheduledTreatmentsRepository()
        contactsRepository = FakeContactsRepository()
        treatmentsRepository = FakeTreatmentsRepository()
        timeTemplatesRepository = FakeTimeTemplateRepository()
        messagesRepository = FakeMessagesRepository()

        runOnUiThread {
            viewModel = AddEditScheduledTreatmentViewModel(contactsRepository, treatmentsRepository, timeTemplatesRepository, messagesRepository, scheduledTreatmentsRepository)
        }

        assertThat(timeTemplatesRepository.insert(timeTemplate), `is`(timeTemplate.timeTemplateId))
        assertThat(messagesRepository.insert(message), `is`(message.messageId))
        assertThat(contactsRepository.insert(contact1), `is`(contact1.contactId))
        assertThat(contactsRepository.insert(contact2), `is`(contact2.contactId))
        assertThat(treatmentsRepository.insert(treatment), `is`(treatment.treatmentId))
    }

    @Test
    fun testDefaultValues() = runTest {

        assert(viewModel.time.value == null)
        assert(viewModel.message.value == null)
        assert(viewModel.timeTemplateText.value == null)
        assert(viewModel.treatment.value == null)
        assert(viewModel.contact.value == null)

    }

    @Test
    fun saveScheduledTreatmentToRepository_STSaved() = runTest {
        val scheduledTreatment = ScheduledTreatment(
            treatmentId = treatment.treatmentId,
            timeTemplateId = timeTemplate.timeTemplateId,
            messageId = message.messageId,
            contactId = contact1.contactId,
            treatmentTime = Calendar.getInstance(),
            scheduledTreatmentId = 0
        )
        runBlocking {
            contactsRepository.insert(contact1)
            timeTemplatesRepository.insert(timeTemplate)
            treatmentsRepository.insert(treatment)
            messagesRepository.insert(message)
        }

        viewModel.start(context = ApplicationProvider.getApplicationContext())

        runOnUiThread {
            viewModel.run {

                runBlocking {
                    setContactById(contact1.contactId)
                    setMessageById(this@AddEditScheduledTreatmentViewModelTest.message.messageId)
                    setTimeTemplateById(timeTemplate.timeTemplateId)
                    setTreatmentById(this@AddEditScheduledTreatmentViewModelTest.treatment.treatmentId)
                    time.value = scheduledTreatment.treatmentTime.timeInMillis
                }
            }

            viewModel.saveScheduledTreatment(ApplicationProvider.getApplicationContext())

            assertThat(
                scheduledTreatmentsRepository.getScheduledTreatments().getOrAwaitValue().first(),
                `is`(scheduledTreatment)
            )
        }

    }
//
//    @Test
//    fun start_noId_keepsDefaultValues() = runBlocking {
//        viewModelScheduled.start(context = ApplicationProvider.getApplicationContext())
//        testDefaultValues()
//    }
//
//    @Test
//    fun start_validId_loadsValues() = runBlocking {
//
//        val time = 100L
//
//        val scheduledTreatment = ScheduledTreatment(
//            id = 11,
//            treatmentId = treatment.treatmentId,
//            treatmentTime = Calendar.getInstance().apply { timeInMillis = time },
//            timeTemplateId = timeTemplate.timeTemplateId,
//            messageId = message.messageId,
//            contactId = contact1.contactId
//        )
//        assertThat(database.scheduledTreatmentDao().insert(scheduledTreatment), `is`(scheduledTreatment.id))
//
//        val treatmentRetrieved = database.treatmentDao().getTreatment(treatment.treatmentId)
//        assertThat(treatmentRetrieved.name, `is`(treatment.name))
//        assertThat(treatmentRetrieved.duration, `is`(treatment.duration))
//        assertThat(treatmentRetrieved.price, `is`(treatment.price))
//
//        val timeTemplates = database.timeTemplateDao().observeTimeTemplates()
//        assertThat(timeTemplates.getOrAwaitValue().first().delay, `is`(timeTemplate.delay))
//
//        val messages = database.messageDao().observeMessages()
//        assertThat(messages.getOrAwaitValue().first().message, `is`(message.message))
//
//        val contacts = database.contactDao().observeAllContacts()
//        assertThat(contacts.getOrAwaitValue().first().name, `is`(contact1.name))
//        assertThat(contacts.getOrAwaitValue().first().phoneNumber, `is`(contact1.phoneNumber))
//
//        viewModelScheduled.start(11, ApplicationProvider.getApplicationContext())
//
//    }

}
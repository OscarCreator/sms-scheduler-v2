package com.oscarcreator.sms_scheduler_v2.addeditscheduledtreatment

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.data.FakeTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.data.contact.Contact
import com.oscarcreator.sms_scheduler_v2.data.contact.DefaultContactsRepository
import com.oscarcreator.sms_scheduler_v2.data.contact.local.ContactsLocalDataSource
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.scheduled.DefaultScheduledTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentContactCrossRef
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.data.scheduled.local.ScheduledTreatmentsLocalDataSource
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import com.oscarcreator.sms_scheduler_v2.util.MainCoroutineRule
import com.oscarcreator.sms_scheduler_v2.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.*
import java.util.*


@ExperimentalCoroutinesApi
class AddEditScheduledTreatmentViewModelTest {


    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    lateinit var database: AppDatabase
    private lateinit var viewModelScheduled: AddEditScheduledTreatmentViewModel

    private val timeTemplate = TimeTemplate(100, timeTemplateId = 6)
    private val message = Message(8, "some old text", true)
    private val receiver1 = Contact( "Bosse", "40602380", contactId = 4)
    private val receiver2 = Contact("Bergit", "0720934592", 4000, contactId = 2)
    private val treatment = Treatment("Treatment 4", 400, 90, treatmentId = 9)

    private lateinit var scheduledTreatmentsRepository: ScheduledTreatmentsRepository

    @Test
    @Before
    fun initializeDatabase() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        scheduledTreatmentsRepository = DefaultScheduledTreatmentsRepository(
            ScheduledTreatmentsLocalDataSource(
                database.scheduledTreatmentDao(),
                database.scheduledTreatmentCrossRefDao()
            )

        )
        val customerRepository = DefaultContactsRepository(
            ContactsLocalDataSource(database.customerDao()))

        val treatmentsRepository = FakeTreatmentsRepository()
        //TODO use fake repositories
        //viewModelScheduled = AddEditScheduledTreatmentViewModel(customerRepository, treatmentsRepository, )

        assertThat(database.timeTemplateDao().insert(timeTemplate), `is`(timeTemplate.timeTemplateId))
        assertThat(database.messageDao().insert(message), `is`(message.id))
        assertThat(database.customerDao().insert(receiver1), `is`(receiver1.contactId))
        assertThat(database.customerDao().insert(receiver2), `is`(receiver2.contactId))
        assertThat(database.treatmentDao().insert(treatment), `is`(treatment.treatmentId))
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testDefaultValues() {

        Assert.assertEquals(viewModelScheduled.time.value, null)
        Assert.assertEquals(viewModelScheduled.message.value, null)
        Assert.assertEquals(viewModelScheduled.timeTemplateText.value, null)
        Assert.assertEquals(viewModelScheduled.treatment.value, null)
        Assert.assertEquals(viewModelScheduled.contacts, mutableListOf<Contact>())
    }

    @Test
    fun observeReceiversUpdatesWhenChanged() {
        val receiver = Contact(name = "Bengt", phoneNumber = "0740203052")
        val expected = listOf(receiver)

        viewModelScheduled.addReceiver(receiver)
        assertThat(viewModelScheduled.contacts, `is`(expected))

        viewModelScheduled.removeReceiver(receiver)

        assertThat(viewModelScheduled.contacts, `is`(emptyList<Contact>()))
    }

    @Test
    fun start_noId_keepsDefaultValues() = runBlocking {
        viewModelScheduled.start(context = ApplicationProvider.getApplicationContext())
        testDefaultValues()
    }

    @Test
    fun start_validId_loadsValues() = runBlocking {
        //



        //https://github.com/android/architecture-samples/blob/main/app/src/test/java/com/example/android/architecture/blueprints/todoapp/addedittask/AddEditTaskViewModelTest.kt


        val time = 100L

        val scheduledTreatment = ScheduledTreatment(
            id = 11,
            treatmentId = treatment.treatmentId,
            treatmentTime = Calendar.getInstance().apply { timeInMillis = time },
            timeTemplateId = timeTemplate.timeTemplateId,
            messageId = message.id
        )
        assertThat(database.scheduledTreatmentDao().insert(scheduledTreatment), `is`(11))
        val scheduledTreatmentCustomerCrossRef =
            ScheduledTreatmentContactCrossRef(11, receiver1.contactId)
        database.scheduledTreatmentCrossRefDao().insert(scheduledTreatmentCustomerCrossRef)

        val customerRetrieved = database.customerDao().getCustomer(receiver1.contactId)
        assertThat(customerRetrieved, `is`(receiver1))
        val treatmentRetrieved = database.treatmentDao().getTreatment(treatment.treatmentId)
        assertThat(treatmentRetrieved, `is`(treatment))
        val timeTemplates = database.timeTemplateDao().observeTimeTemplates()
        assertThat(timeTemplates.getOrAwaitValue(), `is`(listOf(timeTemplate)))
        val messages = database.messageDao().observeMessages()
        assertThat(messages.getOrAwaitValue(), `is`(listOf(message)))
        val crossRef = database.scheduledTreatmentCrossRefDao().getScheduledTreatmentCustomerCrossRefs(11)
        assertThat(crossRef, `is`(listOf(scheduledTreatmentCustomerCrossRef)))

        //viewModelScheduled.start(11)

        //TODO find out why the query to database returns null
        // when the objects clearly is in database
        //assertThat(viewModelScheduled.customers.getOrAwaitValue(), `is`(mutableListOf(receiver1)))

        assertThat(viewModelScheduled.message.getOrAwaitValue(), `is`(messages))
    }

}
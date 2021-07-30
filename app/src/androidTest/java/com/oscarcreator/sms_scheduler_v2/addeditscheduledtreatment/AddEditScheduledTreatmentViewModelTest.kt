package com.oscarcreator.sms_scheduler_v2.addeditscheduledtreatment

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.data.customer.DefaultCustomersRepository
import com.oscarcreator.sms_scheduler_v2.data.customer.local.CustomersLocalDataSource
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.scheduled.DefaultScheduledTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentCustomerCrossRef
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

    private val timeTemplate = TimeTemplate(6, 100)
    private val message = Message(8, "some old text", true)
    private val receiver1 = Customer(4, "Bosse", "40602380")
    private val receiver2 = Customer(2, "Bergit", "0720934592", 4000)
    private val treatment = Treatment(9, "Treatment 4", 400, 90)

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
        val customerRepository = DefaultCustomersRepository(
            CustomersLocalDataSource(database.customerDao()))
        //TODO use fake repositories
        //viewModelScheduled = AddEditScheduledTreatmentViewModel(customerRepository, scheduledTreatmentsRepository)

        assertThat(database.timeTemplateDao().insert(timeTemplate), `is`(timeTemplate.id))
        assertThat(database.messageDao().insert(message), `is`(message.id))
        assertThat(database.customerDao().insert(receiver1), `is`(receiver1.id))
        assertThat(database.customerDao().insert(receiver2), `is`(receiver2.id))
        assertThat(database.treatmentDao().insert(treatment), `is`(treatment.id))
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testDefaultValues() {
        Assert.assertEquals(viewModelScheduled.time.value, null)
        Assert.assertEquals(viewModelScheduled.message.value, null)
        Assert.assertEquals(viewModelScheduled.timeModifier.value, null)
        Assert.assertEquals(viewModelScheduled.treatment.value, null)
        Assert.assertEquals(viewModelScheduled.customers.value, mutableListOf<Customer>())
    }

    @Test
    fun observeReceiversUpdatesWhenChanged() {
        val receiver = Customer(name = "Bengt", phoneNumber = "0740203052")
        val expected = listOf(receiver)

        viewModelScheduled.addReceiver(receiver)
        assertThat(viewModelScheduled.customers.getOrAwaitValue(), `is`(expected))

        viewModelScheduled.removeReceiver(receiver)

        assertThat(viewModelScheduled.customers.getOrAwaitValue(), `is`(emptyList<Customer>()))
    }

    @Test
    fun start_noId_keepsDefaultValues() = runBlocking {
        viewModelScheduled.start()
        testDefaultValues()
    }

    @Test
    fun start_validId_loadsValues() = runBlocking {
        //



        //https://github.com/android/architecture-samples/blob/main/app/src/test/java/com/example/android/architecture/blueprints/todoapp/addedittask/AddEditTaskViewModelTest.kt


        val time = 100L

        val scheduledTreatment = ScheduledTreatment(
            id = 11,
            treatmentId = treatment.id,
            treatmentTime = Calendar.getInstance().apply { timeInMillis = time },
            timeTemplateId = timeTemplate.id,
            messageId = message.id
        )
        assertThat(database.scheduledTreatmentDao().insert(scheduledTreatment), `is`(11))
        val scheduledTreatmentCustomerCrossRef =
            ScheduledTreatmentCustomerCrossRef(11, receiver1.id)
        database.scheduledTreatmentCrossRefDao().insert(scheduledTreatmentCustomerCrossRef)

        val customerRetrieved = database.customerDao().getCustomer(receiver1.id)
        assertThat(customerRetrieved, `is`(receiver1))
        val treatmentRetrieved = database.treatmentDao().getTreatment(treatment.id)
        assertThat(treatmentRetrieved, `is`(treatment))
        val timeTemplates = database.timeTemplateDao().getTimeTemplates()
        assertThat(timeTemplates.getOrAwaitValue(), `is`(listOf(timeTemplate)))
        val messages = database.messageDao().observeMessages()
        assertThat(messages.getOrAwaitValue(), `is`(listOf(message)))
        val crossRef = database.scheduledTreatmentCrossRefDao().getScheduledTreatmentCustomerCrossRefs(11)
        assertThat(crossRef, `is`(listOf(scheduledTreatmentCustomerCrossRef)))

        viewModelScheduled.start(11)

        //TODO find out why the query to database returns null
        // when the objects clearly is in database
        assertThat(viewModelScheduled.customers.getOrAwaitValue(), `is`(mutableListOf(receiver1)))

        assertThat(viewModelScheduled.message.getOrAwaitValue(), `is`(messages))
    }

}
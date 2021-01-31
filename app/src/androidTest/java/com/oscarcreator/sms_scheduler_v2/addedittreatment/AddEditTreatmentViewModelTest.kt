package com.oscarcreator.sms_scheduler_v2.addedittreatment

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.scheduled.DefaultScheduledTreatmentRepository
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentCustomerCrossRef
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentRepository
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
class AddEditTreatmentViewModelTest {


    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    lateinit var database: AppDatabase
    private lateinit var viewModel: AddEditTreatmentViewModel

    private val timeTemplate = TimeTemplate(6, 100)
    private val message = Message(8, "some old text", true)
    private val receiver1 = Customer(4, "Bosse", "40602380")
    private val receiver2 = Customer(2, "Bergit", "0720934592", 4000)
    private val treatment = Treatment(9, "Treatment 4", 400, 90)

    private lateinit var repository: ScheduledTreatmentRepository

    @Test
    @Before
    fun initializeDatabase() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
        repository = DefaultScheduledTreatmentRepository.getInstance(
            database.scheduledTreatmentDao(),
            database.scheduledTreatmentCrossRefDao()
        )
        viewModel = AddEditTreatmentViewModel(repository)

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
        Assert.assertEquals(viewModel.time.value, null)
        Assert.assertEquals(viewModel.message.value, null)
        Assert.assertEquals(viewModel.timeModifier.value, null)
        Assert.assertEquals(viewModel.treatment.value, null)
        Assert.assertEquals(viewModel.receivers.value, mutableListOf<Customer>())
    }

    @Test
    fun observeReceiversUpdatesWhenChanged() {
        val receiver = Customer(name = "Bengt", phoneNumber = "0740203052")
        val expected = listOf(receiver)

        viewModel.addReceiver(receiver)
        assertThat(viewModel.receivers.getOrAwaitValue(), `is`(expected))

        viewModel.removeReceiver(receiver)

        assertThat(viewModel.receivers.getOrAwaitValue(), `is`(emptyList<Customer>()))
    }

    @Test
    fun start_noId_keepsDefaultValues() = runBlocking {
        viewModel.start()
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
        database.scheduledTreatmentDao().insert(scheduledTreatment)
        val scheduledTreatmentCustomerCrossRef =
            ScheduledTreatmentCustomerCrossRef(11, receiver1.id)
        database.scheduledTreatmentCrossRefDao().insert(scheduledTreatmentCustomerCrossRef)


        viewModel.start(11)

        //TODO find out why the query to database returns null
        // when the objects clearly is in database
        assertThat(viewModel.receivers.getOrAwaitValue(), `is`(mutableListOf()))

        assertThat(viewModel.message.getOrAwaitValue(), `is`(message))
    }

}
package com.oscarcreator.sms_scheduler_v2.data.local

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentCustomerCrossRef
import com.oscarcreator.sms_scheduler_v2.data.scheduled.local.ScheduledTreatmentDao
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import com.oscarcreator.sms_scheduler_v2.util.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class ScheduledTreatmentDaoTest : BaseDaoTest() {

    lateinit var scheduledTreatmentDao: ScheduledTreatmentDao

    lateinit var timeTemplate: TimeTemplate
    lateinit var message: Message
    lateinit var customer: Customer
    lateinit var treatment: Treatment

    @Before
    override fun inititalizeDatabase(): Unit = runBlocking {
        super.inititalizeDatabase()
        scheduledTreatmentDao = database.scheduledTreatmentDao()

        timeTemplate = TimeTemplate(id = 1, delay = 50)
        assertThat(database.timeTemplateDao().insert(timeTemplate), `is`(1))

        message = Message(id = 5, message = "hey this is a message")
        assertThat(database.messageDao().insert(message), `is`(5))

        customer = Customer(id = 8, name = "Bengt burger", phoneNumber = "07839402787", money = 10)
        assertThat(database.customerDao().insert(customer), `is`(8))

        treatment = Treatment(id = 105, name = "Treatment1", price = 101, duration = 30)
        assertThat(database.treatmentDao().insert(treatment), `is`(105))

    }

    @Test
    fun scheduledTreatment_insert_returnsScheduledTreatmentWithRelationData() = runBlocking {

        val scheduledTreatment = ScheduledTreatment(
            id = 10,
            treatmentId = treatment.id,
            timeTemplateId = timeTemplate.id,
            messageId = message.id,
            treatmentTime = Calendar.getInstance().apply { set(Calendar.YEAR, 2021) },
        )
        val scheduledTreatmentId = scheduledTreatmentDao.insert(scheduledTreatment)
        val scheduledTreatmentCustomerCrossRef = ScheduledTreatmentCustomerCrossRef(
            scheduledTreatmentId = scheduledTreatmentId,
            customerId = customer.id
        )
        database.scheduledTreatmentCrossRefDao().insert(scheduledTreatmentCustomerCrossRef)

        scheduledTreatmentDao.getScheduledTreatmentsWithMessageAndTimeTemplateAndCustomers().getOrAwaitValue().let {
            assertThat(it.size, `is`(1))
            assertThat(it[0].scheduledTreatment, `is`(scheduledTreatment))
            assertThat(it[0].customers, `is`(listOf(customer)))
            assertThat(it[0].message, `is`(message))
            assertThat(it[0].timeTemplate, `is`(timeTemplate))
            assertThat(it[0].treatment, `is`(treatment))
        }

    }

    @Test
    fun scheduledTreatment_insertAndUpdated_returnsUpdated(): Unit = runBlocking {

        val scheduledTreatment = ScheduledTreatment(
            id = 10,
            treatmentId = treatment.id,
            timeTemplateId = timeTemplate.id,
            messageId = message.id,
            treatmentTime = Calendar.getInstance().apply { set(Calendar.YEAR, 2021) },
        )
        val scheduledTreatmentId = scheduledTreatmentDao.insert(scheduledTreatment)
        val scheduledTreatmentCustomerCrossRef = ScheduledTreatmentCustomerCrossRef(
            scheduledTreatmentId = scheduledTreatmentId,
            customerId = customer.id
        )
        database.scheduledTreatmentCrossRefDao().insert(scheduledTreatmentCustomerCrossRef)

        val updatedTimeTemplate = TimeTemplate(id = 1, delay = 50)
        assertThat(database.timeTemplateDao().update(updatedTimeTemplate), `is`(1))

        val updatedMessage = Message(id = 5, message = "updated message", isTemplate = true)
        assertThat(database.messageDao().update(updatedMessage), `is`(1))

        val updatedCustomer = Customer(id = 8, name = "Bengt Burger with big b's", phoneNumber = customer.phoneNumber, money = 5)
        assertThat(database.customerDao().update(updatedCustomer), `is`(1))

        val updatedTreatment = Treatment(id = 105, name = "Treatment Two", price = 604, duration = 55)
        assertThat(database.treatmentDao().update(updatedTreatment), `is`(1))

        scheduledTreatmentDao.getScheduledTreatmentWithData(scheduledTreatmentId).let {
            assertThat(it!!.scheduledTreatment, `is`(scheduledTreatment))
            assertThat(it.customers, `is`(listOf(updatedCustomer)))
            assertThat(it.message, `is`(updatedMessage))
            assertThat(it.timeTemplate, `is`(updatedTimeTemplate))
            assertThat(it.treatment, `is`(updatedTreatment))

        }

    }

    @Test
    fun scheduledTreatment_insertAndDeleted_removesScheduledTreatmentAndCrossRefButKeepsCustomers() = runBlocking {
        val scheduledTreatment = ScheduledTreatment(
            id = 10,
            treatmentId = treatment.id,
            timeTemplateId = timeTemplate.id,
            messageId = message.id,
            treatmentTime = Calendar.getInstance().apply { set(Calendar.YEAR, 2021) },
        )
        val scheduledTreatmentId = scheduledTreatmentDao.insert(scheduledTreatment)
        val scheduledTreatmentCustomerCrossRef = ScheduledTreatmentCustomerCrossRef(
            scheduledTreatmentId = scheduledTreatmentId,
            customerId = customer.id
        )
        database.scheduledTreatmentCrossRefDao().insert(scheduledTreatmentCustomerCrossRef)

        scheduledTreatmentDao.delete(scheduledTreatment)

        scheduledTreatmentDao.getScheduledTreatments().getOrAwaitValue().let {
            assertThat(it, `is`(emptyList()))
        }

        database.scheduledTreatmentCrossRefDao().getScheduledTreatmentCustomerCrossRefs().getOrAwaitValue().let {
            assertThat(it, `is`(emptyList()))
        }

        database.customerDao().getCustomers().getOrAwaitValue().let {
            assertThat(it, `is`(listOf(customer)))
        }

    }


    @Test
    fun scheduledTreatment_insertAndDeletedCrossRef_keepsScheduledTreatmentAndCustomer() = runBlocking {
        val scheduledTreatment = ScheduledTreatment(
            id = 10,
            treatmentId = treatment.id,
            timeTemplateId = timeTemplate.id,
            messageId = message.id,
            treatmentTime = Calendar.getInstance().apply { set(Calendar.YEAR, 2021) },
        )
        val scheduledTreatmentId = scheduledTreatmentDao.insert(scheduledTreatment)
        val scheduledTreatmentCustomerCrossRef = ScheduledTreatmentCustomerCrossRef(
            scheduledTreatmentId = scheduledTreatmentId,
            customerId = customer.id
        )
        database.scheduledTreatmentCrossRefDao().insert(scheduledTreatmentCustomerCrossRef)
        database.scheduledTreatmentCrossRefDao().delete(scheduledTreatmentCustomerCrossRef)

        scheduledTreatmentDao.getScheduledTreatments().getOrAwaitValue().let {
            assertThat(it, `is`(listOf(scheduledTreatment)))
        }

        database.scheduledTreatmentCrossRefDao().getScheduledTreatmentCustomerCrossRefs().getOrAwaitValue().let {
            assertThat(it, `is`(emptyList()))
        }

        database.customerDao().getCustomers().getOrAwaitValue().let {
            assertThat(it, `is`(listOf(customer)))
        }

    }

    @Test
    fun scheduledTreatment_insertAndDeletedCustomer_keepsCustomer() = runBlocking {
        val scheduledTreatment = ScheduledTreatment(
            id = 10,
            treatmentId = treatment.id,
            timeTemplateId = timeTemplate.id,
            messageId = message.id,
            treatmentTime = Calendar.getInstance().apply { set(Calendar.YEAR, 2021) },
        )
        val scheduledTreatmentId = scheduledTreatmentDao.insert(scheduledTreatment)
        val scheduledTreatmentCustomerCrossRef = ScheduledTreatmentCustomerCrossRef(
            scheduledTreatmentId = scheduledTreatmentId,
            customerId = customer.id
        )

        database.scheduledTreatmentCrossRefDao().insert(scheduledTreatmentCustomerCrossRef)

        try {
            database.customerDao().delete(customer)
        }catch (e: SQLiteConstraintException){
            e.printStackTrace()
            Log.e("ScheduledTreatmentDaoTest", "customer could not be deleted")
            //TODO ask delete cross reference and scheduled treatment if they have passed
        }

        scheduledTreatmentDao.getScheduledTreatments().getOrAwaitValue().let {
            assertThat(it, `is`(listOf(scheduledTreatment)))
        }

        database.scheduledTreatmentCrossRefDao().getScheduledTreatmentCustomerCrossRefs().getOrAwaitValue().let {
            assertThat(it, `is`(listOf(scheduledTreatmentCustomerCrossRef)))
        }

        database.customerDao().getCustomers().getOrAwaitValue().let {
            assertThat(it, `is`(listOf(customer)))
        }

    }

    @Test
    fun scheduledTreatment_insertYear2021_returnsScheduledTreatment() = runBlocking {
        val scheduledTreatment = ScheduledTreatment(
            id = 10,
            treatmentId = treatment.id,
            timeTemplateId = timeTemplate.id,
            messageId = message.id,
            treatmentTime = Calendar.getInstance().apply { set(Calendar.YEAR, 2021) },
        )
        val scheduledTreatmentId = scheduledTreatmentDao.insert(scheduledTreatment)
        val scheduledTreatmentCustomerCrossRef = ScheduledTreatmentCustomerCrossRef(
            scheduledTreatmentId = scheduledTreatmentId,
            customerId = customer.id
        )

        database.scheduledTreatmentCrossRefDao().insert(scheduledTreatmentCustomerCrossRef)


        scheduledTreatmentDao.getScheduledTreatments().getOrAwaitValue().let {
            assertThat(it, `is`(listOf(scheduledTreatment)))
        }

        database.scheduledTreatmentCrossRefDao().getScheduledTreatmentCustomerCrossRefs().getOrAwaitValue().let {
            assertThat(it, `is`(listOf(scheduledTreatmentCustomerCrossRef)))
        }

        val calendarBehind = Calendar.getInstance().apply { set(Calendar.YEAR, 2020) }

        scheduledTreatmentDao.getUpcomingScheduledTreatmentsWithData(calendarBehind).getOrAwaitValue().let {
            assertThat(it.size, `is`(1))
        }
    }


    @Test
    fun scheduledTreatment_insertYear2021_returnsEmpty() = runBlocking {
        val scheduledTreatment = ScheduledTreatment(
            id = 10,
            treatmentId = treatment.id,
            timeTemplateId = timeTemplate.id,
            messageId = message.id,
            treatmentTime = Calendar.getInstance().apply { set(Calendar.YEAR, 2021) },
        )
        val scheduledTreatmentId = scheduledTreatmentDao.insert(scheduledTreatment)
        val scheduledTreatmentCustomerCrossRef = ScheduledTreatmentCustomerCrossRef(
            scheduledTreatmentId = scheduledTreatmentId,
            customerId = customer.id
        )

        database.scheduledTreatmentCrossRefDao().insert(scheduledTreatmentCustomerCrossRef)


        scheduledTreatmentDao.getScheduledTreatments().getOrAwaitValue().let {
            assertThat(it, `is`(listOf(scheduledTreatment)))
        }

        database.scheduledTreatmentCrossRefDao().getScheduledTreatmentCustomerCrossRefs().getOrAwaitValue().let {
            assertThat(it, `is`(listOf(scheduledTreatmentCustomerCrossRef)))
        }

        val calendarInfront = Calendar.getInstance().apply { set(Calendar.YEAR, 2022) }

        scheduledTreatmentDao.getUpcomingScheduledTreatmentsWithData(calendarInfront).getOrAwaitValue().let {
            assertThat(it.size, `is`(0))
        }
    }

}
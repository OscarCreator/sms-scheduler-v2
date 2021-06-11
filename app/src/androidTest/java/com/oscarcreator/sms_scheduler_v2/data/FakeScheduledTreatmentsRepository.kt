package com.oscarcreator.sms_scheduler_v2.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentCustomerCrossRef
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import java.util.*
import kotlin.collections.LinkedHashMap

class FakeScheduledTreatmentsRepository : ScheduledTreatmentsRepository {

    private var scheduledTreatmentsServiceData: LinkedHashMap<Long, ScheduledTreatment> = LinkedHashMap()
    private val observableScheduledTreatments = MutableLiveData<List<ScheduledTreatment>>()

    private var scheduledTreatmentWithDataServiceData: LinkedHashMap<Long, ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers> = LinkedHashMap()
    private val observableScheduledTreatmentWithData = MutableLiveData<List<ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers>>()

    private var scheduledTreatmentCrossRefServiceData: LinkedHashMap<Pair<Long, Long>, ScheduledTreatmentCustomerCrossRef> = LinkedHashMap()

    var message = Message(1, "Some text")
    var timeTemplate = TimeTemplate(4, 30400L)
    var treatment = Treatment(72, "Some treatment", 500, 30)
    var customers: List<Customer> = listOf(Customer(5, "Name", "9383475", 200))

    override fun getScheduledTreatments(): LiveData<List<ScheduledTreatment>> =
        observableScheduledTreatments

    override fun getScheduledTreatmentsWithMessageAndTimeTemplateAndCustomers(): LiveData<List<ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers>> =
        observableScheduledTreatmentWithData


    override fun getUpcomingScheduledTreatments(currentDay: Calendar): LiveData<List<ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers>> {
        TODO("Not yet implemented")
    }

    override suspend fun getScheduledTreatmentWithData(scheduledTreatmentId: Long): ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers? {
        return ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers(scheduledTreatmentsServiceData[scheduledTreatmentId]!!, message, timeTemplate, treatment, customers)
    }

    override suspend fun insert(scheduledTreatment: ScheduledTreatment): Long {
        scheduledTreatmentsServiceData[scheduledTreatment.id] = scheduledTreatment
        return scheduledTreatment.id
    }

    override suspend fun update(scheduledTreatment: ScheduledTreatment): Int {
        scheduledTreatmentsServiceData[scheduledTreatment.id] = scheduledTreatment
        return 1
    }

    override suspend fun insertCrossRef(scheduledTreatmentCustomerCrossRef: ScheduledTreatmentCustomerCrossRef): Long {
        scheduledTreatmentWithDataServiceData[scheduledTreatmentCustomerCrossRef.customerId]
        return scheduledTreatmentCustomerCrossRef.scheduledTreatmentId
    }

    override suspend fun updateCrossRef(scheduledTreatmentCustomerCrossRef: ScheduledTreatmentCustomerCrossRef): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCrossRefs(scheduledTreatmentId: Long) {
        TODO("Not yet implemented")
    }
}
package com.oscarcreator.sms_scheduler_v2.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.oscarcreator.sms_scheduler_v2.data.contact.Contact
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageTimeTemplateAndContact
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import java.util.*
import kotlin.collections.LinkedHashMap

class FakeScheduledTreatmentsRepository : ScheduledTreatmentsRepository {

    private var scheduledTreatmentsServiceData: LinkedHashMap<Long, ScheduledTreatment> = LinkedHashMap()
    private val observableScheduledTreatments = MutableLiveData<List<ScheduledTreatment>>()

    private var scheduledTreatmentWithDataServiceData: LinkedHashMap<Long, ScheduledTreatmentWithMessageTimeTemplateAndContact> = LinkedHashMap()
    private val observableScheduledTreatmentWithData = MutableLiveData<List<ScheduledTreatmentWithMessageTimeTemplateAndContact>>()


    var message = Message( "Some text", messageId = 1)
    var timeTemplate = TimeTemplate(4, timeTemplateId = 30400L)
    var treatment = Treatment("Some treatment", 500, 30, treatmentId = 72)
    var contact: Contact = Contact( "Name", "9383475", 200, contactId = 5)

    override fun getScheduledTreatments(): LiveData<List<ScheduledTreatment>> {
        observableScheduledTreatments.value = scheduledTreatmentsServiceData.values.toList()
        return observableScheduledTreatments
    }


    override fun getScheduledTreatmentsWithMessageAndTimeTemplateAndContacts(): LiveData<List<ScheduledTreatmentWithMessageTimeTemplateAndContact>> =
        observableScheduledTreatmentWithData


    override fun getUpcomingScheduledTreatments(currentDay: Calendar): LiveData<List<ScheduledTreatmentWithMessageTimeTemplateAndContact>> {
        TODO("Not yet implemented")
    }

    override fun getOldScheduledTreatmentsWithData(currentDay: Calendar): LiveData<List<ScheduledTreatmentWithMessageTimeTemplateAndContact>> {
        TODO("Not yet implemented")
    }

    override fun getScheduledTreatment(scheduledTreatmentId: Long): LiveData<ScheduledTreatmentWithMessageTimeTemplateAndContact> {
        TODO("Not yet implemented")
    }

    override suspend fun getScheduledTreatmentWithData(scheduledTreatmentId: Long): ScheduledTreatmentWithMessageTimeTemplateAndContact? {
        return ScheduledTreatmentWithMessageTimeTemplateAndContact(scheduledTreatmentsServiceData[scheduledTreatmentId]!!, message, timeTemplate, treatment, contact)
    }

    override suspend fun getUpcomingScheduledTreatmentsWithData(): List<ScheduledTreatmentWithMessageTimeTemplateAndContact> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(scheduledTreatment: ScheduledTreatment): Long {
        scheduledTreatmentsServiceData[scheduledTreatment.scheduledTreatmentId] = scheduledTreatment
        observableScheduledTreatments.value = scheduledTreatmentsServiceData.values.toList()
        return scheduledTreatment.scheduledTreatmentId
    }

    override suspend fun update(scheduledTreatment: ScheduledTreatment): Int {
        scheduledTreatmentsServiceData[scheduledTreatment.scheduledTreatmentId] = scheduledTreatment
        return 1
    }

    override suspend fun delete(scheduledTreatmentId: Long): Int {
        TODO("Not yet implemented")
    }

}


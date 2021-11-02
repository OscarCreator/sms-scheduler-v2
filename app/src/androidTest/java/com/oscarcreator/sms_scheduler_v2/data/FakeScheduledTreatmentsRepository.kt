package com.oscarcreator.sms_scheduler_v2.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.oscarcreator.sms_scheduler_v2.data.contact.Contact
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentContactCrossRef
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageAndTimeTemplateAndContacts
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import java.util.*
import kotlin.collections.LinkedHashMap

class FakeScheduledTreatmentsRepository : ScheduledTreatmentsRepository {

    private var scheduledTreatmentsServiceData: LinkedHashMap<Long, ScheduledTreatment> = LinkedHashMap()
    private val observableScheduledTreatments = MutableLiveData<List<ScheduledTreatment>>()

    private var scheduledTreatmentWithDataServiceData: LinkedHashMap<Long, ScheduledTreatmentWithMessageAndTimeTemplateAndContacts> = LinkedHashMap()
    private val observableScheduledTreatmentWithData = MutableLiveData<List<ScheduledTreatmentWithMessageAndTimeTemplateAndContacts>>()

    private var scheduledTreatmentCrossRefServiceData: LinkedHashMap<Pair<Long, Long>, ScheduledTreatmentContactCrossRef> = LinkedHashMap()

    var message = Message(1, "Some text")
    var timeTemplate = TimeTemplate(4, 30400L)
    var treatment = Treatment("Some treatment", 500, 30, treatmentId = 72)
    var contacts: List<Contact> = listOf(Contact(5, "Name", "9383475", 200))

    override fun getScheduledTreatments(): LiveData<List<ScheduledTreatment>> =
        observableScheduledTreatments

    override fun getScheduledTreatmentsWithMessageAndTimeTemplateAndContacts(): LiveData<List<ScheduledTreatmentWithMessageAndTimeTemplateAndContacts>> =
        observableScheduledTreatmentWithData


    override fun getUpcomingScheduledTreatments(currentDay: Calendar): LiveData<List<ScheduledTreatmentWithMessageAndTimeTemplateAndContacts>> {
        TODO("Not yet implemented")
    }

    override fun getOldScheduledTreatmentsWithData(currentDay: Calendar): LiveData<List<ScheduledTreatmentWithMessageAndTimeTemplateAndContacts>> {
        TODO("Not yet implemented")
    }

    override fun getScheduledTreatment(scheduledTreatmentId: Long): LiveData<ScheduledTreatmentWithMessageAndTimeTemplateAndContacts> {
        TODO("Not yet implemented")
    }

    override suspend fun getScheduledTreatmentWithData(scheduledTreatmentId: Long): ScheduledTreatmentWithMessageAndTimeTemplateAndContacts? {
        return ScheduledTreatmentWithMessageAndTimeTemplateAndContacts(scheduledTreatmentsServiceData[scheduledTreatmentId]!!, message, timeTemplate, treatment, contacts)
    }

    override suspend fun getUpcomingScheduledTreatmentsWithData(): List<ScheduledTreatmentWithMessageAndTimeTemplateAndContacts> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(scheduledTreatment: ScheduledTreatment): Long {
        scheduledTreatmentsServiceData[scheduledTreatment.id] = scheduledTreatment
        return scheduledTreatment.id
    }

    override suspend fun update(scheduledTreatment: ScheduledTreatment): Int {
        scheduledTreatmentsServiceData[scheduledTreatment.id] = scheduledTreatment
        return 1
    }

    override suspend fun delete(scheduledTreatmentId: Long): Int {
        TODO("Not yet implemented")
    }

    override suspend fun insertCrossRef(scheduledTreatmentContactCrossRef: ScheduledTreatmentContactCrossRef): Long {
        scheduledTreatmentWithDataServiceData[scheduledTreatmentContactCrossRef.contactId]
        return scheduledTreatmentContactCrossRef.scheduledTreatmentId
    }

    override suspend fun updateCrossRef(scheduledTreatmentContactCrossRef: ScheduledTreatmentContactCrossRef): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCrossRefs(scheduledTreatmentId: Long) {
        TODO("Not yet implemented")
    }
}
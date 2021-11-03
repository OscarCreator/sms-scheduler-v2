package com.oscarcreator.sms_scheduler_v2.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplatesRepository
import kotlinx.coroutines.runBlocking

class FakeTimeTemplateRepository : TimeTemplatesRepository {

    var timeTemplatesServiceData: LinkedHashMap<Long, TimeTemplate> = LinkedHashMap()

    private val observableTimeTemplates = MutableLiveData<List<TimeTemplate>>()

    override fun observeAllTimeTemplates(): LiveData<List<TimeTemplate>> {
        runBlocking { observableTimeTemplates.value = timeTemplatesServiceData.values.toList() }
        return observableTimeTemplates
    }

    override fun observeTimeTemplate(id: Long): LiveData<TimeTemplate> {
        runBlocking { observableTimeTemplates.value = timeTemplatesServiceData.values.toList() }
        return observableTimeTemplates.map { timetemplates ->
            timetemplates.first { it.timeTemplateId == id }
        }
    }

    override suspend fun getTimeTemplate(id: Long): Result<TimeTemplate> {
        val timeTemplate = timeTemplatesServiceData[id]
        return if (timeTemplate == null) {
            Result.Error(Exception("Did not find timeTemplate"))
        } else {
            Result.Success(timeTemplate)
        }
    }

    override fun observeTimeTemplates(): LiveData<List<TimeTemplate>> {
        runBlocking { observableTimeTemplates.value = timeTemplatesServiceData.values.toList() }
        return observableTimeTemplates.map { timeTemplates ->
            timeTemplates.filter { !it.toBeDeleted }
        }
    }

    override suspend fun insert(timeTemplate: TimeTemplate): Long {
        // make sure to set your id yourself
        timeTemplatesServiceData[timeTemplate.timeTemplateId] = timeTemplate
        return timeTemplate.timeTemplateId
    }

    override suspend fun delete(vararg timeTemplates: TimeTemplate): Int {
        for (timeTemplate in timeTemplates) {
            timeTemplatesServiceData.remove(timeTemplate.timeTemplateId)
        }
        return timeTemplates.size
    }

    override suspend fun deleteById(timeTemplateId: Long): Int {
        timeTemplatesServiceData.remove(timeTemplateId)
        return 1
    }

    override suspend fun update(timeTemplate: TimeTemplate): Int {
        timeTemplatesServiceData[timeTemplate.timeTemplateId] = timeTemplate
        return 1
    }

    override suspend fun updateToBeDeleted(timeTemplateId: Long) {
        val timeTemplate = timeTemplatesServiceData[timeTemplateId]
        if (timeTemplate != null) {
            timeTemplatesServiceData[timeTemplateId] = TimeTemplate(timeTemplate.delay, true, timeTemplate.timeTemplateId, timeTemplate.timeTemplateVersion, timeTemplate.timeTemplateGroupId)
        }
    }

    override suspend fun updateScheduledTreatmentsWithNewTimeTemplate(
        oldTimeTemplateId: Long,
        newTimeTemplateId: Long
    ) {
        TODO("Not yet implemented")
    }
}
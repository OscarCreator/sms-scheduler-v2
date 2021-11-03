package com.oscarcreator.sms_scheduler_v2.data.timetemplate

import androidx.lifecycle.LiveData
import com.oscarcreator.sms_scheduler_v2.data.Result
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.local.TimeTemplatesLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DefaultTimeTemplatesRepository(
    private val timeTemplatesDataSource: TimeTemplatesLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): TimeTemplatesRepository {

    override suspend fun getTimeTemplate(id: Long): Result<TimeTemplate> = timeTemplatesDataSource.getTimeTemplate(id)

    override fun observeTimeTemplates(): LiveData<List<TimeTemplate>> =
        timeTemplatesDataSource.observeTimeTemplates()


    override suspend fun insert(timeTemplate: TimeTemplate): Long =
        timeTemplatesDataSource.insert(timeTemplate)

    override suspend fun delete(vararg timeTemplate: TimeTemplate): Int =
        timeTemplatesDataSource.delete(*timeTemplate)

    override suspend fun deleteById(timeTemplateId: Long): Int = timeTemplatesDataSource.deleteById(timeTemplateId)

    override suspend fun update(timeTemplate: TimeTemplate): Int =
        timeTemplatesDataSource.update(timeTemplate)

    override suspend fun updateToBeDeleted(timeTemplateId: Long) =
        timeTemplatesDataSource.updateToBeDeleted(timeTemplateId)

    override suspend fun updateScheduledTreatmentsWithNewTimeTemplate(
        oldTimeTemplateId: Long,
        newTimeTemplateId: Long
    ) = timeTemplatesDataSource.updateScheduledTreatmentsWithNewTimeTemplate(oldTimeTemplateId, newTimeTemplateId)

    override fun observeAllTimeTemplates(): LiveData<List<TimeTemplate>> =
        timeTemplatesDataSource.observeAllTimeTemplates()

    override fun observeTimeTemplate(id: Long): LiveData<TimeTemplate> =
        timeTemplatesDataSource.observeTimeTemplate(id)
}
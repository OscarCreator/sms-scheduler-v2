package com.oscarcreator.pigeon.data.timetemplate.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.oscarcreator.pigeon.data.Result
import com.oscarcreator.pigeon.data.scheduled.ScheduledTreatmentWithMessageTimeTemplateAndContact
import com.oscarcreator.pigeon.data.timetemplate.TimeTemplate
import com.oscarcreator.pigeon.data.timetemplate.TimeTemplatesDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TimeTemplatesLocalDataSource internal constructor(
    private val timeTemplatesDao: TimeTemplateDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): TimeTemplatesDataSource {

    override suspend fun getTimeTemplate(id: Long): Result<TimeTemplate> =
        withContext(ioDispatcher) {
            try {
                val timeTemplate = timeTemplatesDao.getTimeTemplate(id)
                if (timeTemplate != null) {
                    Result.Success(timeTemplate)
                } else {
                    Result.Error(Exception("Timetemplate not found!"))
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override fun observeTimeTemplates(): LiveData<List<TimeTemplate>> = timeTemplatesDao.observeTimeTemplates()

    override suspend fun insert(timeTemplate: TimeTemplate): Long = timeTemplatesDao.insert(timeTemplate)

    override suspend fun delete(vararg timeTemplate: TimeTemplate): Int = timeTemplatesDao.delete(*timeTemplate)

    override suspend fun deleteById(timeTemplateId: Long): Int =
        timeTemplatesDao.deleteById(timeTemplateId)

    override suspend fun update(timeTemplate: TimeTemplate): Int = timeTemplatesDao.update(timeTemplate)

    override suspend fun updateToBeDeleted(timeTemplateId: Long) =
        timeTemplatesDao.updateToBeDeleted(timeTemplateId)

    override suspend fun updateScheduledTreatmentsWithNewTimeTemplate(
        oldTimeTemplateId: Long,
        newTimeTemplateId: Long
    ) = timeTemplatesDao.updateScheduledTreatmentsWithNewTimeTemplate(oldTimeTemplateId, newTimeTemplateId)

    override fun observeAllTimeTemplates(): LiveData<List<TimeTemplate>> =
        timeTemplatesDao.observeAllTimeTemplates()

    override fun observeTimeTemplate(id: Long): LiveData<Result<TimeTemplate>> {
        return timeTemplatesDao.observeTimeTemplate(id).map {
            Result.Success(it)
        }
    }

    override fun getScheduledTreatmentsWithTimeTemplateId(timeTemplateId: Long): List<ScheduledTreatmentWithMessageTimeTemplateAndContact> =
        timeTemplatesDao.getScheduledTreatmentsWithTimeTemplateId(timeTemplateId)
}
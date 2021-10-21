package com.oscarcreator.sms_scheduler_v2.data.timetemplate.local

import androidx.lifecycle.LiveData
import com.oscarcreator.sms_scheduler_v2.data.Result
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplatesDataSource
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

    override fun observeTimeTemplates(): LiveData<List<TimeTemplate>> = timeTemplatesDao.getTimeTemplates()

    override suspend fun insert(timeTemplate: TimeTemplate): Long = timeTemplatesDao.insert(timeTemplate)

    override suspend fun delete(vararg timeTemplate: TimeTemplate): Int = timeTemplatesDao.delete(*timeTemplate)

    override suspend fun update(timeTemplate: TimeTemplate): Int = timeTemplatesDao.update(timeTemplate)

    override fun observeTimeTemplate(id: Long): LiveData<TimeTemplate> = timeTemplatesDao.observeTimeTemplate(id)


}
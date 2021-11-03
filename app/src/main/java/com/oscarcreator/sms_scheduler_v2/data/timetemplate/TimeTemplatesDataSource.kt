package com.oscarcreator.sms_scheduler_v2.data.timetemplate

import androidx.lifecycle.LiveData
import com.oscarcreator.sms_scheduler_v2.data.Result

interface TimeTemplatesDataSource {

    fun observeAllTimeTemplates(): LiveData<List<TimeTemplate>>

    fun observeTimeTemplate(id: Long): LiveData<TimeTemplate>

    suspend fun getTimeTemplate(id: Long): Result<TimeTemplate>

    fun observeTimeTemplates(): LiveData<List<TimeTemplate>>

    suspend fun insert(timeTemplate: TimeTemplate): Long

    suspend fun delete(vararg timeTemplate: TimeTemplate): Int

    suspend fun deleteById(timeTemplateId: Long): Int

    suspend fun update(timeTemplate: TimeTemplate): Int

    suspend fun updateToBeDeleted(timeTemplateId: Long)

    suspend fun updateScheduledTreatmentsWithNewTimeTemplate(oldTimeTemplateId: Long, newTimeTemplateId: Long)
}
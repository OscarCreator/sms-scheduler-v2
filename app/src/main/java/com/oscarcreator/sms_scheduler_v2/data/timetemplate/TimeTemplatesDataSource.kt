package com.oscarcreator.sms_scheduler_v2.data.timetemplate

import androidx.lifecycle.LiveData

interface TimeTemplatesDataSource {

    suspend fun getTimeTemplate(id: Long): TimeTemplate

    fun observeTimeTemplates(): LiveData<List<TimeTemplate>>

    suspend fun insert(timeTemplate: TimeTemplate): Long

    suspend fun delete(timeTemplate: TimeTemplate): Int

    suspend fun update(timeTemplate: TimeTemplate): Int

}
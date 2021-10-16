package com.oscarcreator.sms_scheduler_v2.timetemplates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplatesRepository
import com.oscarcreator.sms_scheduler_v2.util.Event

class TimeTemplatesViewModel(
    private val timeTemplatesRepository: TimeTemplatesRepository
) : ViewModel() {

    var selectedCount: Int = 0

    private val _timeTemplates: LiveData<List<TimeTemplate>> = timeTemplatesRepository.observeTimeTemplates()
    val timeTemplates: LiveData<List<TimeTemplate>> = _timeTemplates

    private val _newTimeTemplateEvent = MutableLiveData<Event<Unit>>()
    val newTimeTemplateEvent: LiveData<Event<Unit>> = _newTimeTemplateEvent

    private val _openTimeTemplateEvent = MutableLiveData<Event<Unit>>()
    val openTimeTemplateEvent: LiveData<Event<Unit>> = _openTimeTemplateEvent

    suspend fun deleteTimeTemplates(vararg timeTemplates: TimeTemplate): Int = timeTemplatesRepository.delete(*timeTemplates)

}
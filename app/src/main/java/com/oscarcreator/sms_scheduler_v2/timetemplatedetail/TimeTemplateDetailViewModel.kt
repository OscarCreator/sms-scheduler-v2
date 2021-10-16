package com.oscarcreator.sms_scheduler_v2.timetemplatedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplatesRepository
import com.oscarcreator.sms_scheduler_v2.util.Event

class TimeTemplateDetailViewModel(
    private val timeTemplatesRepository: TimeTemplatesRepository
) : ViewModel() {

    private val _timeTemplateId = MutableLiveData<Long>()

    private val _timeTemplate = _timeTemplateId.switchMap {
        timeTemplatesRepository.observeTimeTemplate(it)
    }

    val timeTemplate: LiveData<TimeTemplate> = _timeTemplate

    private val _editTimeTemplateEvent = MutableLiveData<Event<Unit>>()
    val editTimeTemplateEvent: LiveData<Event<Unit>> = _editTimeTemplateEvent

    fun start(timeTemplateId: Long) {
        _timeTemplateId.value = timeTemplateId
    }

    fun editTimeTemplate() {
        _editTimeTemplateEvent.value = Event(Unit)
    }


}
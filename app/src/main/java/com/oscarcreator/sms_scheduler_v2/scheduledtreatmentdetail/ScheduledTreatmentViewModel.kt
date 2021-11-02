package com.oscarcreator.sms_scheduler_v2.scheduledtreatmentdetail

import android.content.Context
import androidx.lifecycle.*
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageAndTimeTemplateAndContacts
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.util.Event
import com.oscarcreator.sms_scheduler_v2.util.deleteAlarm
import com.oscarcreator.sms_scheduler_v2.util.sendSmsNow
import kotlinx.coroutines.launch

class ScheduledTreatmentViewModel(
    private val scheduledTreatmentsRepository: ScheduledTreatmentsRepository
) : ViewModel(){

    private val _scheduledTreatmentId = MutableLiveData<Long>()

    private val _scheduledTreatment = _scheduledTreatmentId.switchMap {
        scheduledTreatmentsRepository.getScheduledTreatment(it)
    }

    val scheduledTreatment: LiveData<ScheduledTreatmentWithMessageAndTimeTemplateAndContacts> = _scheduledTreatment

    private val _editScheduledTreatmentEvent = MutableLiveData<Event<Unit>>()
    val editScheduledTreatmentEvent: LiveData<Event<Unit>> = _editScheduledTreatmentEvent

    private val _deleteScheduledTreatmentEvent = MutableLiveData<Event<Unit>>()
    val deleteScheduledTreatmentEvent: LiveData<Event<Unit>> = _deleteScheduledTreatmentEvent

    private val _smsSentEvent = MutableLiveData<Event<Unit>>()
    val smsSentEvent: LiveData<Event<Unit>> = _smsSentEvent

    fun editScheduledTreatment() {
        _editScheduledTreatmentEvent.value = Event(Unit)
    }

    fun start(scheduledTreatmentId: Long){
        _scheduledTreatmentId.value = scheduledTreatmentId
    }

    fun delete(context: Context) = viewModelScope.launch {
        _scheduledTreatment.value?.let {
            deleteAlarm(context, it)
            scheduledTreatmentsRepository.delete(it.scheduledTreatment.id)
            _deleteScheduledTreatmentEvent.value = Event(Unit)
        }
    }

    fun sendNow(context: Context) {
        _scheduledTreatment.value?.let {
            deleteAlarm(context, it)
            sendSmsNow(context, it)
            _smsSentEvent.value = Event(Unit)
        }
    }
}
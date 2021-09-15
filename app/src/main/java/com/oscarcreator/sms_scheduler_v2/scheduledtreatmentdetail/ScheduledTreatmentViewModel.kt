package com.oscarcreator.sms_scheduler_v2.scheduledtreatmentdetail

import android.content.Context
import androidx.lifecycle.*
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.util.Event
import com.oscarcreator.sms_scheduler_v2.util.deleteAlarm
import kotlinx.coroutines.launch

class ScheduledTreatmentViewModel(
    private val scheduledTreatmentsRepository: ScheduledTreatmentsRepository
) : ViewModel(){

    private val _scheduledTreatmentId = MutableLiveData<Long>()

    private val _scheduledTreatment = _scheduledTreatmentId.switchMap {
        scheduledTreatmentsRepository.getScheduledTreatment(it)
    }

    val scheduledTreatment: LiveData<ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers> = _scheduledTreatment

    private val _editScheduledTreatmentEvent = MutableLiveData<Event<Unit>>()
    val editScheduledTreatmentEvent = _editScheduledTreatmentEvent

    private val _deleteScheduledTreatmentEvent = MutableLiveData<Event<Unit>>()
    val deleteScheduledTreatmentEvent = _deleteScheduledTreatmentEvent

    fun editScheduledTreatment() {
        editScheduledTreatmentEvent.value = Event(Unit)
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
}
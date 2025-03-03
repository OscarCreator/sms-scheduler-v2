package com.oscarcreator.pigeon.scheduledtreatmentdetail

import android.content.Context
import androidx.lifecycle.*
import com.oscarcreator.pigeon.R
import com.oscarcreator.pigeon.data.Result
import com.oscarcreator.pigeon.data.scheduled.ScheduledTreatmentWithMessageTimeTemplateAndContact
import com.oscarcreator.pigeon.data.scheduled.ScheduledTreatmentsRepository
import com.oscarcreator.pigeon.util.Event
import com.oscarcreator.pigeon.util.deleteAlarm
import com.oscarcreator.pigeon.util.sendSmsNow
import kotlinx.coroutines.launch

class ScheduledTreatmentDetailViewModel(
    private val scheduledTreatmentsRepository: ScheduledTreatmentsRepository
) : ViewModel(){

    private val _scheduledTreatmentId = MutableLiveData<Long>()

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _scheduledTreatment = _scheduledTreatmentId.switchMap {
        scheduledTreatmentsRepository.observeScheduledTreatment(it)
            .map { scheduledTreatment -> computeResult(scheduledTreatment)}
    }

    val scheduledTreatment: LiveData<ScheduledTreatmentWithMessageTimeTemplateAndContact?> = _scheduledTreatment

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
            scheduledTreatmentsRepository.delete(it.scheduledTreatment.scheduledTreatmentId)
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

    private fun computeResult(scheduledTreatment: Result<ScheduledTreatmentWithMessageTimeTemplateAndContact>): ScheduledTreatmentWithMessageTimeTemplateAndContact? {
        return if (scheduledTreatment is Result.Success) {
            scheduledTreatment.data
        } else {
            _snackbarText.value = Event(R.string.loading_appointment_error)
            null
        }
    }
}
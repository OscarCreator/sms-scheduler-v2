package com.oscarcreator.pigeon.timetemplatedetail

import androidx.lifecycle.*
import com.oscarcreator.pigeon.data.Result
import com.oscarcreator.pigeon.data.timetemplate.TimeTemplate
import com.oscarcreator.pigeon.data.timetemplate.TimeTemplatesRepository
import com.oscarcreator.pigeon.util.Event
import kotlinx.coroutines.launch

class TimeTemplateDetailViewModel(
    private val timeTemplatesRepository: TimeTemplatesRepository
) : ViewModel() {

    private val _timeTemplateId = MutableLiveData<Long>()

    private val _timeTemplate = _timeTemplateId.switchMap { timeTemplateId ->
        timeTemplatesRepository.observeTimeTemplate(timeTemplateId)
            .map { computeResult(it) }
    }

    val timeTemplate: LiveData<TimeTemplate?> = _timeTemplate

    private val _editTimeTemplateEvent = MutableLiveData<Event<Unit>>()
    val editTimeTemplateEvent: LiveData<Event<Unit>> = _editTimeTemplateEvent

    private val _deleteTimeTemplateEvent = MutableLiveData<Event<Unit>>()
    val deleteTimeTemplateEvent: LiveData<Event<Unit>> = _deleteTimeTemplateEvent

    fun start(timeTemplateId: Long) {
        _timeTemplateId.value = timeTemplateId
    }

    fun deleteTimeTemplate() = viewModelScope.launch {
        _timeTemplateId.value?.let {
            try {
                timeTemplatesRepository.deleteById(it)
            } catch (e: Exception) {
                timeTemplatesRepository.updateToBeDeleted(it)
            }
            // TODO snackbar
            _deleteTimeTemplateEvent.value = Event(Unit)

        }

    }

    fun editTimeTemplate() {
        _editTimeTemplateEvent.value = Event(Unit)
    }

    private fun computeResult(timeTemplateResult: Result<TimeTemplate>): TimeTemplate? {
        return if (timeTemplateResult is Result.Success) {
            timeTemplateResult.data
        } else {
            //TODO snackbar
            null
        }
    }

}
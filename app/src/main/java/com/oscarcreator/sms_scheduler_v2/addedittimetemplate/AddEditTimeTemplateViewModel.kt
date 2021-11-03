package com.oscarcreator.sms_scheduler_v2.addedittimetemplate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.Result
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplatesRepository
import com.oscarcreator.sms_scheduler_v2.util.Event
import kotlinx.coroutines.launch

class AddEditTimeTemplateViewModel(
    private val timeTemplatesRepository: TimeTemplatesRepository
): ViewModel() {

    var minutes = 0
    var hours = 0
    var days = 0
    var switchState = true

    private val _timeTemplateUpdatedEvent = MutableLiveData<Event<Long>>()
    val timeTemplateUpdatedEvent: LiveData<Event<Long>> = _timeTemplateUpdatedEvent

    private val _timeTemplateLoadedEvent = MutableLiveData<Event<Unit>>()
    val timeTemplateLoadedEvent: LiveData<Event<Unit>> = _timeTemplateLoadedEvent

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private var timeTemplateId: Long = -1L
    private var timeTemplateGroupId: String = ""
    private var timeTemplateVersion: Long = -1L
    private var isNewTimeTemplate = false
    private var isDataLoaded = false

    fun start(timeTemplateId: Long = -1L) {
        if (timeTemplateId == -1L) {
            isNewTimeTemplate = true
            return
        }
        if (isDataLoaded) {
            // already initialized
            return
        }

        this.timeTemplateId = timeTemplateId
        isNewTimeTemplate = false
        _dataLoading.value = true

        viewModelScope.launch {
            timeTemplatesRepository.getTimeTemplate(timeTemplateId).let {
                if (it is Result.Success) {
                    onTimeTemplateLoaded(it.data)
                }else {
                    onTimeTemplateNotAvailable()
                }
            }
        }
    }

    fun saveTimeTemplate() {
        var currentDelay = days * 86_400_000L + hours * 3_600_000L + minutes * 60_000L
        if (switchState) {
            currentDelay *= -1
        }

        if (currentDelay == 0L) {
            _snackbarText.value = Event(R.string.time_template_missing_data)
            return
        }

        val currentTimeTemplateId = timeTemplateId
        if (isNewTimeTemplate || currentTimeTemplateId == -1L) {
            createTimeTemplate(TimeTemplate(delay = currentDelay))
        } else {
            updateTimeTemplate(TimeTemplate(
                currentDelay,
                timeTemplateVersion = timeTemplateVersion,
                timeTemplateGroupId = timeTemplateGroupId
                ))
        }
    }

    private fun onTimeTemplateLoaded(timeTemplate: TimeTemplate) {
        var tempDelay = timeTemplate.delay
        if (tempDelay < 0) {
            switchState = true
            tempDelay *= -1
        } else {
            switchState = false
        }

        days = (tempDelay / 86_400_000L).toInt()
        tempDelay -= days * 86_400_000L
        hours = (tempDelay / 3_600_000L).toInt()
        tempDelay -= hours * 3_600_000L
        minutes = (tempDelay / 60_000L).toInt()

        timeTemplateGroupId = timeTemplate.timeTemplateGroupId
        timeTemplateVersion = timeTemplate.timeTemplateVersion

        _dataLoading.value = false
        _timeTemplateLoadedEvent.value = Event(Unit)
    }

    private fun onTimeTemplateNotAvailable() {
        _dataLoading.value = false
    }

    private fun createTimeTemplate(newTimeTemplate: TimeTemplate) = viewModelScope.launch {
        val newTimeTemplateId = timeTemplatesRepository.insert(newTimeTemplate)
        _timeTemplateUpdatedEvent.value = Event(newTimeTemplateId)
    }

    private fun updateTimeTemplate(timeTemplate: TimeTemplate) = viewModelScope.launch {

        //TODO only upcoming, update()
        // else mark as "deleted" and insert() and updateST()

        try {
            timeTemplatesRepository.deleteById(timeTemplateId)

        } catch (e: Exception) {
            timeTemplatesRepository.updateToBeDeleted(timeTemplateId)
        }

        val newTimeTemplateId = timeTemplatesRepository.insert(timeTemplate)
        timeTemplatesRepository.updateScheduledTreatmentsWithNewTimeTemplate(timeTemplateId, newTimeTemplateId)

        _timeTemplateUpdatedEvent.value = Event(newTimeTemplateId)
    }
}
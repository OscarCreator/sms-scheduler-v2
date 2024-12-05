package com.oscarcreator.pigeon.timetemplates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oscarcreator.pigeon.R
import com.oscarcreator.pigeon.data.timetemplate.TimeTemplate
import com.oscarcreator.pigeon.data.timetemplate.TimeTemplatesRepository
import com.oscarcreator.pigeon.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private val _deleteMessagesEvent = MutableLiveData<Event<Unit>>()
    val deleteMessageEvent: LiveData<Event<Unit>> = _deleteMessagesEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    fun deleteTimeTemplates(vararg timeTemplates: TimeTemplate) = viewModelScope.launch {
        // TODO redo without try,catch
        for ((index, timetemplate) in timeTemplates.withIndex()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    timeTemplatesRepository.deleteById(timetemplate.timeTemplateId)
                } catch (e: Exception) {
                    timeTemplatesRepository.updateToBeDeleted(timetemplate.timeTemplateId)
                } finally {
                    withContext(Dispatchers.Main) {
                        if (index == timeTemplates.size - 1) {
                            _snackbarText.value = if (timeTemplates.size > 1) Event(R.string.time_templates_deleted)
                                else Event(R.string.time_template_delete)
                            _deleteMessagesEvent.value = Event(Unit)
                        }
                    }
                }
            }
        }
    }
}
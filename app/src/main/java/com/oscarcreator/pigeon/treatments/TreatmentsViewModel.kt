package com.oscarcreator.pigeon.treatments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oscarcreator.pigeon.data.treatment.Treatment
import com.oscarcreator.pigeon.data.treatment.TreatmentsRepository
import com.oscarcreator.pigeon.util.Event

class TreatmentsViewModel(
    private val treatmentsRepository: TreatmentsRepository
) : ViewModel() {

    val treatments: LiveData<List<Treatment>> = treatmentsRepository.observeTreatments()

    private val _newTreatmentEvent = MutableLiveData<Event<Unit>>()
    val newTreatmentEvent: LiveData<Event<Unit>> = _newTreatmentEvent

    private val _openTreatmentEvent = MutableLiveData<Event<Long>>()
    val openTreatmentEvent: LiveData<Event<Long>> = _openTreatmentEvent

    /**
     * Is called when add treatment FAB is clicked
     */
    fun addNewTreatment() {
        _newTreatmentEvent.value = Event(Unit)
    }

    /**
     * Is called when treatment item in list is clicked
     * */
    fun openTreatment(treatmentId: Long) {
        _openTreatmentEvent.value = Event(treatmentId)
    }
}
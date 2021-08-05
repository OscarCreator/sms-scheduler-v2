package com.oscarcreator.sms_scheduler_v2.addedittreatment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.Result
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import com.oscarcreator.sms_scheduler_v2.data.treatment.TreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.util.Event
import kotlinx.coroutines.launch

class AddEditTreatmentViewModel(
    private val treatmentsRepository: TreatmentsRepository
): ViewModel() {

    companion object {
        private const val TAG = "AddEditTreatmentViewModel"
    }

    val name = MutableLiveData<String>()
    val price = MutableLiveData<String>()
    val duration = MutableLiveData<String>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _treatmentUpdatedEvent = MutableLiveData<Event<Unit>>()
    val treatmentUpdatedEvent: LiveData<Event<Unit>> = _treatmentUpdatedEvent

    private var treatmentId: Long = -1L
    private var isNewTreatment = false
    private var isDataLoaded = false

    fun start(treatmentId: Long = -1) {
        if (treatmentId == -1L) {
            isNewTreatment = true
            return
        }
        if (isDataLoaded) {
            //Already initialized
            return
        }

        this.treatmentId = treatmentId
        isNewTreatment = false

        _dataLoading.value = true

        viewModelScope.launch {
            treatmentsRepository.getTreatment(treatmentId).let {
                if (it is Result.Success) {
                    onTreatmentLoaded(it.data)
                } else {
                    onDataNotAvailable()
                }
            }
        }
    }

    fun saveTreatment() {
        val currentName = name.value
        val currentPrice = price.value
        val currentDuration = duration.value

        if (currentName == null || currentName.isEmpty() ||
            currentPrice == null || currentPrice.isEmpty() ||
            currentDuration == null || currentDuration.isEmpty()) {
            _snackbarText.value = Event(R.string.empty_treatment_message)
            return
        }

        val currentTreatmentId = treatmentId
        if (isNewTreatment || currentTreatmentId == -1L) {
            createTreatment(Treatment(duration = currentDuration.toInt(), name = currentName, price = currentPrice.toInt()))
        } else {
            updateTreatment(Treatment(currentTreatmentId, currentName, currentPrice.toInt(), currentDuration.toInt()))
        }

    }

    private fun onTreatmentLoaded(treatment: Treatment) {
        name.value = treatment.name
        price.value = treatment.price.toString()
        duration.value = treatment.duration.toString()
        _dataLoading.value = false
    }

    private fun onDataNotAvailable() {
        _dataLoading.value = false
    }

    private fun createTreatment(newTreatment: Treatment) = viewModelScope.launch {
        treatmentsRepository.insert(newTreatment)
        _treatmentUpdatedEvent.value = Event(Unit)
    }

    private fun updateTreatment(treatment: Treatment) = viewModelScope.launch {
        treatmentsRepository.update(treatment)
        _treatmentUpdatedEvent.value = Event(Unit)

    }

}
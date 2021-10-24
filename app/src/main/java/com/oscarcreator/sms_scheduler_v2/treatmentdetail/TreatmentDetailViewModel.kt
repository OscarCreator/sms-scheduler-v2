package com.oscarcreator.sms_scheduler_v2.treatmentdetail

import androidx.lifecycle.*
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.Result
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import com.oscarcreator.sms_scheduler_v2.data.treatment.TreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.util.Event
import kotlinx.coroutines.launch

class TreatmentDetailViewModel(
    private val treatmentsRepository: TreatmentsRepository
): ViewModel() {

    private val _treatmentId = MutableLiveData<Long>()

    private val _treatment = _treatmentId.switchMap { treatmentId ->
        treatmentsRepository.observeTreatment(treatmentId)
            .map { computeResult(it) }
    }
    val treatment: LiveData<Treatment?> = _treatment

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _editTreatmentEvent = MutableLiveData<Event<Unit>>()
    val editTreatmentEvent: LiveData<Event<Unit>> = _editTreatmentEvent

    private val _deleteTreatmentEvent = MutableLiveData<Event<Unit>>()
    val deleteTreatmentEvent: LiveData<Event<Unit>> = _deleteTreatmentEvent

    fun start(treatmentId: Long) {
        if (_dataLoading.value == true || treatmentId == _treatmentId.value){
            // already loaded
            return
        }

        _treatmentId.value = treatmentId
    }

    fun deleteTreatment(onException: () -> Unit) = viewModelScope.launch {
        _treatmentId.value?.let {
            try {
                treatmentsRepository.deleteById(it)
                _deleteTreatmentEvent.value = Event(Unit)
            } catch (e: Exception) {
                onException()
            }
        }

    }

    private fun computeResult(treatmentResult: Result<Treatment>): Treatment? {
        return if (treatmentResult is Result.Success) {
            treatmentResult.data
        } else {
            _snackbarText.value = Event(R.string.loading_treatment_error)
            null
        }
    }

}
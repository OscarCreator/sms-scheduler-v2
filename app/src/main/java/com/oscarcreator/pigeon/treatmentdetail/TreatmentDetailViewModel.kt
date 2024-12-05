package com.oscarcreator.pigeon.treatmentdetail

import android.content.Context
import androidx.lifecycle.*
import com.oscarcreator.pigeon.R
import com.oscarcreator.pigeon.data.Result
import com.oscarcreator.pigeon.data.treatment.Treatment
import com.oscarcreator.pigeon.data.treatment.TreatmentsRepository
import com.oscarcreator.pigeon.settings.SettingsFragment
import com.oscarcreator.pigeon.util.Event
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

    var currency: String? = ""

    fun start(treatmentId: Long, context: Context) {
        if (_dataLoading.value == true || treatmentId == _treatmentId.value){
            // already loaded
            return
        }

        currency = context.getSharedPreferences(
            SettingsFragment.SETTINGS_SHARED_PREF, Context.MODE_PRIVATE)
            .getString(
                SettingsFragment.CURRENCY_TAG, context
                    .getString(R.string.default_currency))

        _treatmentId.value = treatmentId
    }

    fun editTreatment() {
        _editTreatmentEvent.value = Event(Unit)
    }

    fun deleteTreatment() = viewModelScope.launch {
        _treatmentId.value?.let {
            try {
                treatmentsRepository.deleteById(it)
            } catch (e: Exception) {
                treatmentsRepository.updateToBeDeleted(it)
            }
            _snackbarText.value = Event(R.string.treatment_deleted)
            _deleteTreatmentEvent.value = Event(Unit)
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
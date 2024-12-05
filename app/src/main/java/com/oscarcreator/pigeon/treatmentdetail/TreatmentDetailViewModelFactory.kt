package com.oscarcreator.pigeon.treatmentdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.pigeon.data.treatment.TreatmentsRepository

class TreatmentDetailViewModelFactory(
    private val treatmentsRepository: TreatmentsRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TreatmentDetailViewModel(
            treatmentsRepository
        ) as T
    }

}
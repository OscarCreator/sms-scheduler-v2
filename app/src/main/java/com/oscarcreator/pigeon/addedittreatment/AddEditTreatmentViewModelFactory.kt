package com.oscarcreator.pigeon.addedittreatment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.pigeon.data.treatment.TreatmentsRepository

class AddEditTreatmentViewModelFactory(
    private val treatmentsRepository: TreatmentsRepository
): ViewModelProvider.NewInstanceFactory() {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddEditTreatmentViewModel(
            treatmentsRepository
        ) as T
    }

}

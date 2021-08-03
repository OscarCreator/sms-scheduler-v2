package com.oscarcreator.sms_scheduler_v2.addedittreatment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.sms_scheduler_v2.data.treatment.TreatmentsRepository

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

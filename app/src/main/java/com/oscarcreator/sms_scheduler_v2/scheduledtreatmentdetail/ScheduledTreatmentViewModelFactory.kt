package com.oscarcreator.sms_scheduler_v2.scheduledtreatmentdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentsRepository

class ScheduledTreatmentViewModelFactory(
    private val scheduledTreatmentsRepository: ScheduledTreatmentsRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ScheduledTreatmentDetailViewModel(scheduledTreatmentsRepository) as T
    }
}
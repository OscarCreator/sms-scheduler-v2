package com.oscarcreator.pigeon.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.pigeon.data.scheduled.ScheduledTreatmentsRepository

class NotificationsViewModelFactory(
    private val scheduledTreatmentsRepository: ScheduledTreatmentsRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotificationsViewModel(
            scheduledTreatmentsRepository
        ) as T
    }
}
package com.oscarcreator.pigeon.dashboard

import androidx.lifecycle.ViewModel
import com.oscarcreator.pigeon.data.scheduled.ScheduledTreatmentsRepository
import java.util.*

class UpcomingTreatmentCardListViewModel(repository: ScheduledTreatmentsRepository) : ViewModel() {

    private val calendar = Calendar.getInstance(Locale.getDefault()).apply {
        set(Calendar.HOUR_OF_DAY, 0)

    }

    val upcomingTreatments = repository.getUpcomingScheduledTreatments(
        calendar
    )

}
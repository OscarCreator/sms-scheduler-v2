package com.oscarcreator.sms_scheduler_v2.dashboard

import androidx.lifecycle.ViewModel
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentRepository
import java.util.*

class UpcomingTreatmentCardListViewModel(repository: ScheduledTreatmentRepository) : ViewModel() {

    private val calendar = Calendar.getInstance(Locale.getDefault()).apply {
        set(Calendar.HOUR_OF_DAY, 0)

    }

    val upcomingTreatments = repository.getUpcomingScheduledTreatments(
        calendar
    )

}
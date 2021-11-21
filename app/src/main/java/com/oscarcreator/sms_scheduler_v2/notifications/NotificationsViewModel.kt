package com.oscarcreator.sms_scheduler_v2.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.data.scheduled.SmsStatus
import com.oscarcreator.sms_scheduler_v2.data.scheduled.TreatmentStatus
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val scheduledTreatmentsRepository: ScheduledTreatmentsRepository
) : ViewModel() {
    fun markScheduledTreatmentAsSent(scheduledTreatmentId: Long) = viewModelScope.launch {
        scheduledTreatmentsRepository.setScheduledTreatmentSmsStatus(scheduledTreatmentId, SmsStatus.RECEIVED)
    }

    fun markScheduledTreatmentAsDone(scheduledTreatmentId: Long) = viewModelScope.launch {
        scheduledTreatmentsRepository.setScheduledTreatmentTreatmentStatus(scheduledTreatmentId, TreatmentStatus.DONE)
    }

    val failedScheduleTreatmentsWithData = scheduledTreatmentsRepository.getUpcomingFailedScheduledTreatmentsWithData()

}
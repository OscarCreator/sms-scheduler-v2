package com.oscarcreator.sms_scheduler_v2.addeditscheduledtreatment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.sms_scheduler_v2.data.customer.CustomersRepository
import com.oscarcreator.sms_scheduler_v2.data.message.MessagesRepository
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplatesRepository
import com.oscarcreator.sms_scheduler_v2.data.treatment.TreatmentsRepository

class AddEditScheduledTreatmentViewModelFactory(
    private val customersRepository: CustomersRepository,
    private val treatmentsRepository: TreatmentsRepository,
    private val timeTemplatesRepository: TimeTemplatesRepository,
    private val messagesRepository: MessagesRepository,
    private val scheduledTreatmentsRepository: ScheduledTreatmentsRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddEditScheduledTreatmentViewModel(
            customersRepository,
            treatmentsRepository,
            timeTemplatesRepository,
            messagesRepository,
            scheduledTreatmentsRepository
        ) as T
    }
}
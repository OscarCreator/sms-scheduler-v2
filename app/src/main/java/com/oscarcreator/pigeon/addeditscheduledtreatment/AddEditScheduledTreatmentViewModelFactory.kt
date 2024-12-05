package com.oscarcreator.pigeon.addeditscheduledtreatment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.pigeon.data.contact.ContactsRepository
import com.oscarcreator.pigeon.data.message.MessagesRepository
import com.oscarcreator.pigeon.data.scheduled.ScheduledTreatmentsRepository
import com.oscarcreator.pigeon.data.timetemplate.TimeTemplatesRepository
import com.oscarcreator.pigeon.data.treatment.TreatmentsRepository

class AddEditScheduledTreatmentViewModelFactory(
    private val contactsRepository: ContactsRepository,
    private val treatmentsRepository: TreatmentsRepository,
    private val timeTemplatesRepository: TimeTemplatesRepository,
    private val messagesRepository: MessagesRepository,
    private val scheduledTreatmentsRepository: ScheduledTreatmentsRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddEditScheduledTreatmentViewModel(
            contactsRepository,
            treatmentsRepository,
            timeTemplatesRepository,
            messagesRepository,
            scheduledTreatmentsRepository
        ) as T
    }
}
package com.oscarcreator.sms_scheduler_v2

import android.app.Application
import com.oscarcreator.sms_scheduler_v2.data.contact.ContactsRepository
import com.oscarcreator.sms_scheduler_v2.data.message.MessagesRepository
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplatesRepository
import com.oscarcreator.sms_scheduler_v2.data.treatment.TreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.util.ServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class SmsSchedulerApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val messagesRepository: MessagesRepository
        get() = ServiceLocator.provideMessagesRepository(this, applicationScope)

    val contactsRepository: ContactsRepository
        get() = ServiceLocator.provideCustomersRepository(this, applicationScope)

    val treatmentsRepository: TreatmentsRepository
        get() = ServiceLocator.provideTreatmentsRepository(this, applicationScope)

    val timeTemplatesRepository: TimeTemplatesRepository
        get() = ServiceLocator.provideTimeTemplatesRepository(this, applicationScope)

    val scheduledTreatmentsRepository: ScheduledTreatmentsRepository
        get() = ServiceLocator.provideScheduledTreatmentsRepository(this, applicationScope)
}
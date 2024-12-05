package com.oscarcreator.pigeon

import android.app.Application
import com.oscarcreator.pigeon.data.contact.ContactsRepository
import com.oscarcreator.pigeon.data.message.MessagesRepository
import com.oscarcreator.pigeon.data.scheduled.ScheduledTreatmentsRepository
import com.oscarcreator.pigeon.data.timetemplate.TimeTemplatesRepository
import com.oscarcreator.pigeon.data.treatment.TreatmentsRepository
import com.oscarcreator.pigeon.util.ServiceLocator
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
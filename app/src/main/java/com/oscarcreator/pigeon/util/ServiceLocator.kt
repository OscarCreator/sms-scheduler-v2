package com.oscarcreator.pigeon.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.oscarcreator.pigeon.data.AppDatabase
import com.oscarcreator.pigeon.data.contact.ContactsDataSource
import com.oscarcreator.pigeon.data.contact.ContactsRepository
import com.oscarcreator.pigeon.data.contact.DefaultContactsRepository
import com.oscarcreator.pigeon.data.contact.local.ContactsLocalDataSource
import com.oscarcreator.pigeon.data.message.DefaultMessagesRepository
import com.oscarcreator.pigeon.data.message.MessagesDataSource
import com.oscarcreator.pigeon.data.message.MessagesRepository
import com.oscarcreator.pigeon.data.message.local.MessagesLocalDataSource
import com.oscarcreator.pigeon.data.scheduled.DefaultScheduledTreatmentsRepository
import com.oscarcreator.pigeon.data.scheduled.ScheduledTreatmentsDataSource
import com.oscarcreator.pigeon.data.scheduled.ScheduledTreatmentsRepository
import com.oscarcreator.pigeon.data.scheduled.local.ScheduledTreatmentsLocalDataSource
import com.oscarcreator.pigeon.data.timetemplate.DefaultTimeTemplatesRepository
import com.oscarcreator.pigeon.data.timetemplate.TimeTemplatesRepository
import com.oscarcreator.pigeon.data.timetemplate.local.TimeTemplatesLocalDataSource
import com.oscarcreator.pigeon.data.treatment.DefaultTreatmentsRepository
import com.oscarcreator.pigeon.data.treatment.TreatmentsRepository
import com.oscarcreator.pigeon.data.treatment.local.TreatmentsLocalDataSource
import kotlinx.coroutines.CoroutineScope

object ServiceLocator {

    private val lock = Any()
    private var database: AppDatabase? = null

    @Volatile
    var messagesRepository: MessagesRepository? = null

    @Volatile
    var contactsRepository: ContactsRepository? = null

    @Volatile
    var scheduledTreatmentsRepository: ScheduledTreatmentsRepository? = null

    @Volatile
    var treatmentsRepository: TreatmentsRepository? = null

    @Volatile
    var timeTemplatesRepository: TimeTemplatesRepository? = null

    fun provideMessagesRepository(context: Context, scope: CoroutineScope): MessagesRepository {
        synchronized(this){
            return messagesRepository ?: createMessagesRepository(context, scope)
        }
    }

    private fun createMessagesRepository(context: Context, scope: CoroutineScope): MessagesRepository {
        val newRepository = DefaultMessagesRepository(createMessagesLocalDataSource(context, scope))
        messagesRepository = newRepository
        return newRepository
    }

    private fun createMessagesLocalDataSource(context: Context, scope: CoroutineScope): MessagesDataSource {
        val database = provideDatabase(context, scope)
        return MessagesLocalDataSource(database.messageDao())
    }


    fun provideCustomersRepository(context: Context, scope: CoroutineScope): ContactsRepository {
        synchronized(this){
            return contactsRepository ?: createCustomersRepository(context, scope)

        }
    }

    private fun createCustomersRepository(context: Context, scope: CoroutineScope): ContactsRepository {
        val newRepository = DefaultContactsRepository(createCustomersLocalDataSource(context, scope))
        contactsRepository = newRepository
        return newRepository
    }

    private fun createCustomersLocalDataSource(context: Context, scope: CoroutineScope): ContactsDataSource {
        val database = provideDatabase(context, scope)
        return ContactsLocalDataSource(database.contactDao())
    }

    fun provideScheduledTreatmentsRepository(context: Context, scope: CoroutineScope): ScheduledTreatmentsRepository {
        synchronized(this) {
            return scheduledTreatmentsRepository ?: createScheduledTreatmentsRepository(context, scope)
        }
    }

    private fun createScheduledTreatmentsRepository(context: Context, scope: CoroutineScope): ScheduledTreatmentsRepository {
        val newRepository = DefaultScheduledTreatmentsRepository(
            createScheduledTreatmentsLocalDataSource(context, scope)
        )
        scheduledTreatmentsRepository = newRepository
        return newRepository
    }

    private fun createScheduledTreatmentsLocalDataSource(context: Context, scope: CoroutineScope): ScheduledTreatmentsDataSource {
        val database = provideDatabase(context, scope)
        return ScheduledTreatmentsLocalDataSource(database.scheduledTreatmentDao())
    }

    fun provideTreatmentsRepository(
        context: Context,
        scope: CoroutineScope,
    ): TreatmentsRepository {
        synchronized(this) {
            return treatmentsRepository ?: createTreatmentsRepository(context, scope)

        }
    }

    private fun createTreatmentsRepository(context: Context, scope: CoroutineScope): TreatmentsRepository {
        val newRepository = DefaultTreatmentsRepository(
            createTreatmentLocalDataSource(context, scope)
        )
        treatmentsRepository = newRepository
        return newRepository
    }

    private fun createTreatmentLocalDataSource(context: Context, scope: CoroutineScope): TreatmentsLocalDataSource {
        val database = provideDatabase(context, scope)
        return TreatmentsLocalDataSource(database.treatmentDao())
    }

    fun provideTimeTemplatesRepository(
        context: Context,
        scope: CoroutineScope,
    ): TimeTemplatesRepository {
        synchronized(this) {
            return timeTemplatesRepository ?: createTimeTemplatesRepository(context, scope)
        }
    }

    private fun createTimeTemplatesRepository(context: Context, scope: CoroutineScope): TimeTemplatesRepository {
        val newRepository = DefaultTimeTemplatesRepository(
            createTimeTemplatesLocalDataSource(context, scope)
        )
        timeTemplatesRepository = newRepository
        return newRepository
    }

    private fun createTimeTemplatesLocalDataSource(context: Context, scope: CoroutineScope): TimeTemplatesLocalDataSource {
        val database = provideDatabase(context, scope)
        return TimeTemplatesLocalDataSource(database.timeTemplateDao())
    }

    private fun provideDatabase(context: Context, scope: CoroutineScope): AppDatabase {
        return database ?: createDatabase(context, scope)
    }

    private fun createDatabase(context: Context, scope: CoroutineScope): AppDatabase {
        return AppDatabase.getDatabase(context, scope)
    }


    @VisibleForTesting
    fun resetRepositories(){
        synchronized(lock) {
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            messagesRepository = null
            contactsRepository = null
            treatmentsRepository = null
            scheduledTreatmentsRepository = null
            timeTemplatesRepository = null

        }
    }



}
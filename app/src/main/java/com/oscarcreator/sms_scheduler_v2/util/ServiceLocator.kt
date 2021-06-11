package com.oscarcreator.sms_scheduler_v2.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.data.customer.CustomersDataSource
import com.oscarcreator.sms_scheduler_v2.data.customer.CustomersRepository
import com.oscarcreator.sms_scheduler_v2.data.customer.DefaultCustomersRepository
import com.oscarcreator.sms_scheduler_v2.data.customer.local.CustomersLocalDataSource
import com.oscarcreator.sms_scheduler_v2.data.message.DefaultMessagesRepository
import com.oscarcreator.sms_scheduler_v2.data.message.MessagesDataSource
import com.oscarcreator.sms_scheduler_v2.data.message.MessagesRepository
import com.oscarcreator.sms_scheduler_v2.data.message.local.MessagesLocalDataSource
import com.oscarcreator.sms_scheduler_v2.data.scheduled.DefaultScheduledTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentsDataSource
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.data.scheduled.local.ScheduledTreatmentsLocalDataSource
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.DefaultTimeTemplatesRepository
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplatesRepository
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.local.TimeTemplatesLocalDataSource
import com.oscarcreator.sms_scheduler_v2.data.treatment.DefaultTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.data.treatment.TreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.data.treatment.local.TreatmentsLocalDataSource
import kotlinx.coroutines.CoroutineScope

object ServiceLocator {

    private val lock = Any()
    private var database: AppDatabase? = null

    @Volatile
    var messagesRepository: MessagesRepository? = null

    @Volatile
    var customersRepository: CustomersRepository? = null

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


    fun provideCustomersRepository(context: Context, scope: CoroutineScope): CustomersRepository {
        synchronized(this){
            return customersRepository ?: createCustomersRepository(context, scope)

        }
    }

    private fun createCustomersRepository(context: Context, scope: CoroutineScope): CustomersRepository {
        val newRepository = DefaultCustomersRepository(createCustomersLocalDataSource(context, scope))
        customersRepository = newRepository
        return newRepository
    }

    private fun createCustomersLocalDataSource(context: Context, scope: CoroutineScope): CustomersDataSource {
        val database = provideDatabase(context, scope)
        return CustomersLocalDataSource(database.customerDao())
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
        return ScheduledTreatmentsLocalDataSource(
            database.scheduledTreatmentDao(),
            database.scheduledTreatmentCrossRefDao())
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
            customersRepository = null
            treatmentsRepository = null
            scheduledTreatmentsRepository = null
            timeTemplatesRepository = null

        }
    }



}
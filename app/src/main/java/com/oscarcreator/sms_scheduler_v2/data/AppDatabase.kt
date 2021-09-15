package com.oscarcreator.sms_scheduler_v2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.data.customer.local.CustomerDao
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.message.local.MessagesDao
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentCustomerCrossRef
import com.oscarcreator.sms_scheduler_v2.data.scheduled.local.ScheduledTreatmentCustomerCrossRefDao
import com.oscarcreator.sms_scheduler_v2.data.scheduled.local.ScheduledTreatmentDao
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.local.TimeTemplateDao
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import com.oscarcreator.sms_scheduler_v2.data.treatment.local.TreatmentDao
import com.oscarcreator.sms_scheduler_v2.util.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [
        Customer::class,
        Treatment::class,
        Message::class,
        TimeTemplate::class,
        ScheduledTreatment::class,
        ScheduledTreatmentCustomerCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun treatmentDao(): TreatmentDao
    abstract fun messageDao(): MessagesDao
    abstract fun timeTemplateDao(): TimeTemplateDao
    abstract fun scheduledTreatmentDao(): ScheduledTreatmentDao
    abstract fun scheduledTreatmentCrossRefDao(): ScheduledTreatmentCustomerCrossRefDao

    private class TaskDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database)
                }
            }
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database)
                }
            }
        }

        suspend fun populateDatabase(database: AppDatabase) {
            database.customerDao().insert(Customer(1, "Bengt", "04053824"))
            database.customerDao().insert(Customer(2, "Jerry", "3759324567"))
        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // if the INSTANCE is not null, then return it,
        // if it is, then create the database

        fun getDatabase(
            context: Context,
            scope: CoroutineScope,
        ): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            )
                .addCallback(TaskDatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                .also {
                    INSTANCE = it
                }
        }
    }
}
package com.oscarcreator.pigeon.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.oscarcreator.pigeon.data.contact.Contact
import com.oscarcreator.pigeon.data.contact.local.ContactDao
import com.oscarcreator.pigeon.data.message.Message
import com.oscarcreator.pigeon.data.message.local.MessagesDao
import com.oscarcreator.pigeon.data.scheduled.ScheduledTreatment
import com.oscarcreator.pigeon.data.scheduled.local.ScheduledTreatmentDao
import com.oscarcreator.pigeon.data.timetemplate.TimeTemplate
import com.oscarcreator.pigeon.data.timetemplate.local.TimeTemplateDao
import com.oscarcreator.pigeon.data.treatment.Treatment
import com.oscarcreator.pigeon.data.treatment.local.TreatmentDao
import com.oscarcreator.pigeon.util.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [
        Contact::class,
        Treatment::class,
        Message::class,
        TimeTemplate::class,
        ScheduledTreatment::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao
    abstract fun treatmentDao(): TreatmentDao
    abstract fun messageDao(): MessagesDao
    abstract fun timeTemplateDao(): TimeTemplateDao
    abstract fun scheduledTreatmentDao(): ScheduledTreatmentDao

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
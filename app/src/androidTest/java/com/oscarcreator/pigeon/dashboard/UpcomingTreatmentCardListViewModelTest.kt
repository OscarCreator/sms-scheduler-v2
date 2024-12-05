package com.oscarcreator.pigeon.dashboard

import android.content.Context
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oscarcreator.pigeon.data.AppDatabase
import com.oscarcreator.pigeon.data.scheduled.DefaultScheduledTreatmentsRepository
import com.oscarcreator.pigeon.data.scheduled.local.ScheduledTreatmentsLocalDataSource
import com.oscarcreator.pigeon.util.getOrAwaitValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class UpcomingTreatmentCardListViewModelTest {

    @Rule
    @JvmField
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var database: AppDatabase
    private lateinit var viewModel: UpcomingTreatmentCardListViewModel
    @Before
    fun initializeDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        viewModel = UpcomingTreatmentCardListViewModel(
            DefaultScheduledTreatmentsRepository(ScheduledTreatmentsLocalDataSource(
                database.scheduledTreatmentDao())))
    }

    @Test
    fun test_defaultValue() {
        assertThat("Treatment are not empty", viewModel.upcomingTreatments.getOrAwaitValue().isEmpty())
    }

    @After
    fun closeDb() {
        countingTaskExecutorRule.drainTasks(10, TimeUnit.SECONDS)
        database.close()
    }


}
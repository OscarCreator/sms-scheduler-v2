package com.oscarcreator.sms_scheduler_v2.timetemplate

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.oscarcreator.sms_scheduler_v2.FakeTimeTemplateRepository
import com.oscarcreator.sms_scheduler_v2.MainCoroutineRule
import com.oscarcreator.sms_scheduler_v2.addedittimetemplate.AddEditTimeTemplateViewModel
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddEditTimeTemplateViewModelTest {

    private lateinit var addEditTimeTemplateViewModel: AddEditTimeTemplateViewModel

    private lateinit var repository: FakeTimeTemplateRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        repository = FakeTimeTemplateRepository()
        addEditTimeTemplateViewModel = AddEditTimeTemplateViewModel(repository)
    }

    @Test
    fun saveTimeTemplateToRepository_timeTemplateSaved() {
        addEditTimeTemplateViewModel.start()

        addEditTimeTemplateViewModel.apply {
            minutes = 1
            hours = 4
            days = 3
            switchState = true
        }

        addEditTimeTemplateViewModel.saveTimeTemplate()

        val timeTemplate = repository.timeTemplatesServiceData.values.first()

        assertThat(timeTemplate.delay, `is`(-(60_000 * 1 + 3_600_000L * 4 + 86_400_000L * 3)))
    }

    @Test
    fun loadTimeTemplate_dataShown() {
        val timeTemplate = TimeTemplate(1000 * 60 * 60 * 32, timeTemplateId = 5)

        runBlocking {
            repository.insert(timeTemplate)
        }

        addEditTimeTemplateViewModel.start(timeTemplate.timeTemplateId)

        assertThat(addEditTimeTemplateViewModel.hours, `is`(8))
        assertThat(addEditTimeTemplateViewModel.days, `is`(1))

    }

}
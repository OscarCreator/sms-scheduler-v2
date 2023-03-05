package com.oscarcreator.sms_scheduler_v2.timetemplate

import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oscarcreator.sms_scheduler_v2.MainActivity
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.FakeTimeTemplateRepository
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.util.ServiceLocator
import com.oscarcreator.sms_scheduler_v2.util.toTimeTemplateText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimeTemplateFlowTest {

    private lateinit var repository: FakeTimeTemplateRepository

    @Before
    fun initRepository() {
        repository = FakeTimeTemplateRepository()
        ServiceLocator.timeTemplatesRepository = repository
    }

    @ExperimentalCoroutinesApi
    @After
    fun cleanupDb() = runBlocking {
        ServiceLocator.resetRepositories()
    }

    @Test
    fun displayTimeTemplate_whenRepositoryHasData() {
        val timeTemplate = TimeTemplate(1000 * 60 * 60 * 56)
        runBlocking { repository.insert(timeTemplate) }

        val activityScenario = launchActivity()

        onView(withText(timeTemplate.delay.toTimeTemplateText()))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun deleteTimeTemplate() {
        val timeTemplate = TimeTemplate(1000 * 60 * 60 * 21)
        runBlocking { repository.insert(timeTemplate) }

        val activityScenario = launchActivity()

        onView(withText(timeTemplate.delay.toTimeTemplateText()))
            .check(matches(isDisplayed()))
            .perform(longClick())

        assertThat(repository.timeTemplatesServiceData.size, `is`(1))

        onView(withId(R.id.delete))
            .perform(click())

        assertThat(repository.timeTemplatesServiceData.size, `is`(0))

        activityScenario.close()
    }

    @Test
    fun deleteMultipleTimeTemplates() {
        val timeTemplate1 = TimeTemplate(1000 * 60 * 60 * 23, timeTemplateId = 1)
        val timeTemplate2 = TimeTemplate(-1000 * 60 * 60 * 13, timeTemplateId = 2)

        runBlocking {
            repository.insert(timeTemplate1)
            repository.insert(timeTemplate2)
        }

        val activityScenario = launchActivity()

        onView(withText(timeTemplate1.delay.toTimeTemplateText()))
            .check(matches(isDisplayed()))
            .perform(longClick())

        onView(withText(timeTemplate2.delay.toTimeTemplateText()))
            .check(matches(isDisplayed()))
            .perform(click())

        assertThat(repository.timeTemplatesServiceData.size, `is`(2))

        onView(withId(R.id.delete))
            .perform(click())

        assertThat(repository.timeTemplatesServiceData.size, `is`(0))

        activityScenario.close()
    }

    @Test
    fun addNewTimeTemplate_isClicked_navigateToAddEditFragment_andBack() {
        val activityScenario = launchActivity()

        onView(withId(R.id.fab_add_timetemplate))
            .perform(click())

        activityScenario.onActivity {
            assertThat(it.findNavController(R.id.nav_host_fragment).currentDestination?.id, `is`(R.id.addEditTimeTemplateFragment))
        }

        pressBack()

        activityScenario.onActivity {
            assertThat(it.findNavController(R.id.nav_host_fragment).currentDestination?.id, `is`(R.id.timeTemplateListFragment))
        }

        activityScenario.close()
    }

    @Test
    fun validTimeTemplate_isSaved_andNavigatesBack() {

        val activityScenario = launchActivity()

        onView(withId(R.id.fab_add_timetemplate))
            .perform(click())

        onView(withId(R.id.np_days)).perform(swipeUp())
        onView(withId(R.id.np_hours)).perform(swipeUp())
        onView(withId(R.id.np_minutes)).perform(swipeUp())

        onView(withId(R.id.fab_save_time_template)).perform(click())

        assertThat(repository.timeTemplatesServiceData.size, `is`(1))

        activityScenario.onActivity {
            assertThat(it.findNavController(R.id.nav_host_fragment).currentDestination?.id, `is`(R.id.timeTemplateListFragment))
        }

        activityScenario.close()
    }

    @Test
    fun emptyTimeTemplate_isNotSaved() {
        val activityScenario = launchActivity()

        onView(withId(R.id.fab_add_timetemplate))
            .perform(click())

        onView(withId(R.id.fab_save_time_template)).perform(click())

        assertThat(repository.timeTemplatesServiceData.size, `is`(0))

        activityScenario.onActivity {
            assertThat(it.findNavController(R.id.nav_host_fragment).currentDestination?.id, `is`(R.id.addEditTimeTemplateFragment))
        }

        activityScenario.close()
    }

    private fun launchActivity(): ActivityScenario<MainActivity> {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        activityScenario.onActivity { activity ->
            activity.findNavController(R.id.nav_host_fragment).navigate(R.id.timeTemplateListFragment)
        }
        return activityScenario
    }
}
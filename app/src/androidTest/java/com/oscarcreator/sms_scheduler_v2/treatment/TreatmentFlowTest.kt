package com.oscarcreator.sms_scheduler_v2.treatment

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oscarcreator.sms_scheduler_v2.MainActivity
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.FakeTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import com.oscarcreator.sms_scheduler_v2.util.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TreatmentFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var repository: FakeTreatmentsRepository

    @Before
    fun initRepository() {
        repository = FakeTreatmentsRepository()
        ServiceLocator.treatmentsRepository = repository
    }

    @ExperimentalCoroutinesApi
    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepositories()
    }

    @Test
    fun displayTreatment_whenRepositoryHasData() {
        val treatment = Treatment(1, "Treatment 1", 400, 50)
        runBlocking {
            repository.insert(treatment)
        }

        val activityScenario = launchActivity()
        composeTestRule.onNodeWithText(treatment.name)
            .assertIsDisplayed()

        activityScenario.close()
    }

    @Test
    fun deleteTreatment() {
        val treatment = Treatment(1, "Treatment 1", 400, 50)
        runBlocking {
            repository.insert(treatment)
        }

        val activityScenario = launchActivity()

        composeTestRule.onNodeWithText(treatment.name)
            .assertIsDisplayed()
            .performClick()

        onView(withId(R.id.delete))
            .perform(click())

        assertThat(repository.treatmentsServiceData.size, `is`(0))

        //TODO idling resource
        //composeTestRule.onNodeWithText(treatment.name).assertDoesNotExist()

        activityScenario.close()
    }

    @Test
    fun addNewTreatment_isClicked_navigatesToAddEditFragment_andBack() {
        val activityScenario = launchActivity()

        composeTestRule.onNodeWithContentDescription("Add new treatment")
            .assertIsDisplayed()
            .performClick()

        activityScenario.onActivity {
            assertThat(it.findNavController(R.id.nav_host_fragment).currentDestination?.id, `is`(R.id.addEditTreatment))
        }

        activityScenario.close()
    }

    @Test
    fun validTreatment_isSaved_andNavigatesBack() {
        val treatment = Treatment(1, "Treatment 1", 400, 50)

        val activityScenario = launchActivity()

        composeTestRule.onNodeWithContentDescription("Add new treatment")
            .assertIsDisplayed()
            .performClick()

        onView(withId(R.id.tv_treatment_name))
            .check(matches(isDisplayed()))
            .perform(typeText(treatment.name))

        onView(withId(R.id.tv_duration))
            .check(matches(isDisplayed()))
            .perform(typeText(treatment.duration.toString()))

        onView(withId(R.id.tv_price))
            .check(matches(isDisplayed()))
            .perform(typeText(treatment.price.toString()))
            .perform(pressImeActionButton())

        onView(withId(R.id.fab_save_treatment))
            .check(matches(isDisplayed()))
            .perform(click())

        repository.treatmentsServiceData.values.first().apply {
            assertThat(name, `is`(treatment.name))
            assertThat(price, `is`(treatment.price))
            assertThat(duration, `is`(treatment.duration))
        }

        activityScenario.onActivity {
            assertThat(it.findNavController(R.id.nav_host_fragment).currentDestination?.id, `is`(R.id.treatmentsFragment))
        }

        activityScenario.close()

    }

    @Test
    fun invalidTreatment_isNotSaved() {
        val treatment = Treatment(1, "Treatment 1", 400, 50)

        val activityScenario = launchActivity()

        composeTestRule.onNodeWithContentDescription("Add new treatment")
            .assertIsDisplayed()
            .performClick()

        onView(withId(R.id.tv_treatment_name))
            .check(matches(isDisplayed()))
            .perform(typeText(treatment.name))
            .perform(pressBack())

        onView(withId(R.id.fab_save_treatment))
            .check(matches(isDisplayed()))
            .perform(click())

        assertThat(repository.treatmentsServiceData.values.size, `is`(0))

        activityScenario.close()
    }

    private fun launchActivity(): ActivityScenario<MainActivity> {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        activityScenario.onActivity { activity ->
            activity.findNavController(R.id.nav_host_fragment).navigate(R.id.treatmentsFragment)
        }
        return activityScenario
    }

}
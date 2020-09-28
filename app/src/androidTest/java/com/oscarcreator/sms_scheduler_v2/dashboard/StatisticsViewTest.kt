package com.oscarcreator.sms_scheduler_v2.dashboard

import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oscarcreator.sms_scheduler_v2.MainActivity
import com.oscarcreator.sms_scheduler_v2.MainActivityViewModel
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.TreatmentsStatsRepository
import com.oscarcreator.sms_scheduler_v2.util.withMaterialText
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatisticsViewTest {

    @get:Rule
    var mActivityTestRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var mainActivityViewModel: MainActivityViewModel

    private lateinit var instrumentationContext: Context

    @Before
    fun setup(){
        mainActivityViewModel = MainActivityViewModel(TreatmentsStatsRepository.getInstance())
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun viewIsDisplayed(){
        onView(withId(R.id.statistics_view)).check(matches(isDisplayed()))
    }

    @Test
    fun totalTreatments_isDisplayed() {
        val treatments = 100
        mainActivityViewModel.setTotalTreatments(treatments)
        onView(withId(R.id.tv_total_treatments)).check(matches(withMaterialText(treatments.toString())))
    }

    @Test
    fun procentTreatments_isDisplayed() {
        val procent = 11
        mainActivityViewModel.setProcentTreatments(procent)
        val text = instrumentationContext.getString(R.string.increase_procent_treatments, procent)
        onView(withId(R.id.tv_procent_treatments)).check(matches(withMaterialText(text)))

    }

    @Test
    fun totalEarnings_isDisplayed() {
        val earnings = 4300
        mainActivityViewModel.setTotalEarnings(earnings)
        val text = instrumentationContext.getString(R.string.total_earning, earnings)
        onView(withId(R.id.tv_total_earnings)).check(matches(withMaterialText(text)))
    }

    @Test
    fun totalEarningsExclusive_isDisplayed() {
        val earningsExclusive = 1400
        mainActivityViewModel.setTotalEarningsExclusive(earningsExclusive)
        val text = instrumentationContext.getString(R.string.total_earning_exclusive, earningsExclusive)
        onView(withId(R.id.tv_total_earnings_exclusive)).check(matches(withMaterialText(text)))
    }
}
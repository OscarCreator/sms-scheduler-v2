package com.oscarcreator.pigeon.dashboard

import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oscarcreator.pigeon.MainActivity
import com.oscarcreator.pigeon.R
import com.oscarcreator.pigeon.util.withMaterialButtonText
import com.oscarcreator.pigeon.util.withMaterialText
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpcomingTreatmentCardListFragmentTest {


    @get:Rule
    var activityScenarioRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var context: Context

    @Before
    fun setup() = runBlocking {
        context = InstrumentationRegistry.getInstrumentation().targetContext

    }

    @Test
    fun view_isDisplayed(){
        onView(withId(R.id.upcoming_treatment_list_card_view)).check(matches(isDisplayed()))
    }

    @Test
    fun viewAllButton_isDisplayedAndWithCorrectText(){
        onView(withId(R.id.btn_view_all)).check(matches(isDisplayed()))
        val text = context.getString(R.string.view_all)
        onView(withId(R.id.btn_view_all)).check(matches(withMaterialButtonText(text)))
    }

    @Test
    fun title_isDisplayedAndWithCorrectText(){
        onView(withId(R.id.upcoming_treatment_list_title)).check(matches(isDisplayed()))
        val text = context.getString(R.string.upcoming)
        onView(withId(R.id.upcoming_treatment_list_title)).check(matches(withMaterialText(text)))
    }

    //TODO add item for this to be displayed
    @Test
    fun recyclerview_isDisplayed(){
        onView(withId(R.id.cv_appointments)).check(matches(not(isDisplayed())))
    }

}
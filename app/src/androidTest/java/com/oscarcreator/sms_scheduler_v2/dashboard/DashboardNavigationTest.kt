package com.oscarcreator.sms_scheduler_v2.dashboard

import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oscarcreator.sms_scheduler_v2.MainActivity
import com.oscarcreator.sms_scheduler_v2.R
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DashboardNavigationTest {

    @get: Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun navigateTo_addEditTreatment(){
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withParent(Matchers.isA(Toolbar::class.java)),
                Matchers.isA(AppCompatTextView::class.java)))
            .check(ViewAssertions.matches(ViewMatchers.withText("Dashboard")))

        onView(withId(R.id.fab_add_treatment))
            .perform(ViewActions.click())

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withParent(Matchers.isA(Toolbar::class.java)),
                Matchers.isA(AppCompatTextView::class.java)))
            .check(ViewAssertions.matches(ViewMatchers.withText("AddEdit")))

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withParent(Matchers.isA(Toolbar::class.java)),
                Matchers.isA(AppCompatImageButton::class.java),
                ViewMatchers.withContentDescription("Navigate up")
            )
        ).perform(ViewActions.click())

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withParent(Matchers.isA(Toolbar::class.java)),
                Matchers.isA(AppCompatTextView::class.java)))
            .check(ViewAssertions.matches(ViewMatchers.withText("Dashboard")))

    }

    @Test
    fun testNavigationToAddEditTreatmentFragment() {
        // Create a TestNavHostController
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)

        // Create a graphical FragmentScenario for the Dashboard
        val dashboardScenario = launchFragmentInContainer<DashboardFragment>(themeResId = R.style.AppTheme)

        // Set the NavController property on the fragment
        dashboardScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        // Verify that performing a click changes the NavController’s state
        onView(withId(R.id.fab_add_treatment)).perform(click())
        assertThat(navController.currentDestination?.id, `is`(R.id.addEditTreatmentFragment))

    }


}

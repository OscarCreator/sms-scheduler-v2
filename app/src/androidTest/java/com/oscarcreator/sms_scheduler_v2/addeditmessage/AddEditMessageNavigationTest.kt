package com.oscarcreator.sms_scheduler_v2.addeditmessage

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oscarcreator.sms_scheduler_v2.MainActivity
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.addeditscheduledtreatment.AddEditScheduledTreatmentFragment
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddEditMessageNavigationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun navigateTo_addEditMessage(){
        navigateToMessageListFragment()

        Espresso.onView(ViewMatchers.withId(R.id.fab_add_message))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun bottomSheet_itemClicked_isVisible(){
        navigateToMessageListFragment()

        Espresso.onView(ViewMatchers.withText("A sample message with only one line"))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.btn_use))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }


    private fun navigateToMessageListFragment(){
        Espresso.onView(ViewMatchers.withId(R.id.fab_add_treatment))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.btn_message))
            .perform(ViewActions.click())
    }

    //not working because of findNavController().previousBackStackEntry?.savedStateHandle?.set()
    //@Test
    fun test_navigationController_navigation(){
        // Create a TestNavHostController
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)


        // Create a graphical FragmentScenario for the AddEditTreatment
        val addEditTreatmentScenario = launchFragmentInContainer<AddEditScheduledTreatmentFragment>(themeResId = R.style.AppTheme)

        // Set the NavController property on the fragment
        addEditTreatmentScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        Espresso.closeSoftKeyboard()

        // Verify that performing a click changes the NavController’s state
        Espresso.onView(ViewMatchers.withId(R.id.btn_message))
            .perform(ViewActions.click())
        MatcherAssert.assertThat(navController.currentDestination?.id,
            CoreMatchers.`is`(R.id.messageListFragment))

    }

}
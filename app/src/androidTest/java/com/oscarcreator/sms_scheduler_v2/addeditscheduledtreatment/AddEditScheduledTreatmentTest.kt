package com.oscarcreator.sms_scheduler_v2.addeditscheduledtreatment

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.google.android.material.chip.Chip
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.FakeScheduledTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class AddEditScheduledTreatmentTest {

    private lateinit var repository: ScheduledTreatmentsRepository

    @Before
    fun initRepository(): Unit = runBlocking {
        repository = FakeScheduledTreatmentsRepository()
    }

    //TODO not sure if this is really good or not to manipulate the database in the application
    @After
    fun removeData(): Unit = runBlocking {

    }

    @Test
    fun linearLayoutContactList_isDisplayed(){
        Espresso.onView(ViewMatchers.withId(R.id.ll_contacts_list))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun linearLayoutTime_isDisplayed(){
        Espresso.onView(ViewMatchers.withId(R.id.ll_time))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun textInputLayoutTreatments_isDisplayed(){
        Espresso.onView(ViewMatchers.withId(R.id.til_treatments))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun materialButtonTimeTemplate_isDisplayed(){
        Espresso.onView(ViewMatchers.withId(R.id.btn_timetemplate))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun materialButtonMessageTemplate_isDisplayed(){
        Espresso.onView(ViewMatchers.withId(R.id.btn_message))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }



    @Test
    fun autocompleteRecyclerView_isDisplayed(){

        Espresso.onView(ViewMatchers.withId(R.id.et_contact_input))
            .perform(ViewActions.typeText("Bengt"))

        Espresso.onView(ViewMatchers.withId(R.id.rv_autocomplete_list))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun autocompleteRecyclerView_chooseItem() {
        // write text
        Espresso.onView(ViewMatchers.withId(R.id.et_contact_input))
            .perform(ViewActions.typeText("Bengt"))

        // click the autocompleted item
        Espresso.onView(ViewMatchers.withText("Bengt Bengtsson"))
                .perform(ViewActions.click())

        // check if autocomplete recyclerview is hidden
        Espresso.onView(ViewMatchers.withId(R.id.rv_autocomplete_list))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))

        // check if it converted to an chip
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withText("Bengt Bengtsson"),
            ViewMatchers.isDisplayed()))
                .check(ViewAssertions.matches(Matchers.isA(Chip::class.java)))

        // check if text is deleted from edittext field
        Espresso.onView(ViewMatchers.withId(R.id.et_contact_input))
            .check(ViewAssertions.matches(ViewMatchers.withText("")))
    }

    @Test
    fun contactList_deleteContactWithBackspace(){

        val contactName = "Bengt Bengtsson"

        // write text
        Espresso.onView(ViewMatchers.withId(R.id.et_contact_input))
            .perform(ViewActions.typeText("Bengt"))

        // click the autocompleted item
        Espresso.onView(ViewMatchers.withText("Bengt Bengtsson"))
            .perform(ViewActions.click())

        // check if added view is of type chip
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withText(contactName),
            ViewMatchers.withParent(ViewMatchers.isDisplayed())))
            .check(ViewAssertions.matches(Matchers.isA(Chip::class.java)))

        // write delete to edittext
        Espresso.onView(ViewMatchers.withId(R.id.et_contact_input))
            .perform(ViewActions.pressKey(KeyEvent.KEYCODE_DEL))

        // has deleted the chip
        Espresso.onView(ViewMatchers.withId(R.id.fl_contacts))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(1)))

    }

    @Test
    fun contactList_writeSpaceAsFirstCharacter_isNotValid(){
        Espresso.onView(ViewMatchers.withId(R.id.et_contact_input))
            .perform(ViewActions.typeText("     "))
            .check(ViewAssertions.matches(ViewMatchers.withText("")))
    }

    private fun launchFragment(navController: TestNavHostController, bundle: Bundle) {
        runOnUiThread {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.addEditScheduledTreatmentFragment)
        }
        val scenario = launchFragmentInContainer<AddEditScheduledTreatmentFragment>(bundle)

        scenario.onFragment {
            Navigation.setViewNavController(it.requireView(), navController)
        }
    }


}
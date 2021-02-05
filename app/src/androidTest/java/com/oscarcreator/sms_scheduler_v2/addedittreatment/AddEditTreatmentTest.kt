package com.oscarcreator.sms_scheduler_v2.addedittreatment

import android.content.Context
import android.view.KeyEvent
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.chip.Chip
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddEditTreatmentTest {

    lateinit var addEditTreatmentScenario: FragmentScenario<AddEditTreatmentFragment>
    private lateinit var context: Context

    private lateinit var database: AppDatabase

    private val customer = Customer(14, "Bengt Bengtsson", "903849047839")

    @Before
    fun setup(): Unit = runBlocking {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)
        database = AppDatabase.getDatabase(context, scope = this)

        addEditTreatmentScenario = launchFragmentInContainer<AddEditTreatmentFragment>(themeResId = R.style.AppTheme)

        // Set the NavController property on the fragment
        addEditTreatmentScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }


        database.customerDao().insert(customer)


    }

    //TODO not sure if this is really good or not to manipulate the database in the application
    @After
    fun removeData(): Unit = runBlocking {
        database.customerDao().delete(customer)
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


}
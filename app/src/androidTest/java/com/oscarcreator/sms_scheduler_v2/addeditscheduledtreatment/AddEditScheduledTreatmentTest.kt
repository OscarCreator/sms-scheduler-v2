package com.oscarcreator.sms_scheduler_v2.addeditscheduledtreatment

import android.Manifest
import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.oscarcreator.sms_scheduler_v2.MainActivity
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.dashboard.DashboardFragmentDirections
import com.oscarcreator.sms_scheduler_v2.data.FakeContactsRepository
import com.oscarcreator.sms_scheduler_v2.data.FakeScheduledTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.util.ServiceLocator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class AddEditScheduledTreatmentTest {

    //TODO redo with activityScenario

    @Rule
    @JvmField
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.SEND_SMS)

    private lateinit var repository: ScheduledTreatmentsRepository
    private lateinit var contactsRepository: FakeContactsRepository

    @Before
    fun initRepository(): Unit = runBlocking {
        repository = FakeScheduledTreatmentsRepository()
        contactsRepository = FakeContactsRepository()
        ServiceLocator.scheduledTreatmentsRepository = repository
        ServiceLocator.contactsRepository = contactsRepository
    }

    @After
    fun cleanupDb() {
        ServiceLocator.resetRepositories()
    }

    @Test
    fun linearLayoutContactList_isDisplayed(){
        val activityScenario = launchActivity()
        Espresso.onView(ViewMatchers.withId(R.id.ll_contacts_list))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        activityScenario.close()
    }

    @Test
    fun linearLayoutTime_isDisplayed(){
        val activityScenario = launchActivity()
        Espresso.onView(ViewMatchers.withId(R.id.ll_time))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        activityScenario.close()
    }

    @Test
    fun textInputLayoutTreatments_isDisplayed(){
        val activityScenario = launchActivity()
        Espresso.onView(ViewMatchers.withId(R.id.ll_treatment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        activityScenario.close()
    }

    @Test
    fun materialButtonTimeTemplate_isDisplayed(){
        val activityScenario = launchActivity()
        Espresso.onView(ViewMatchers.withId(R.id.btn_timetemplate))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        activityScenario.close()
    }

    @Test
    fun materialButtonMessageTemplate_isDisplayed(){
        val activityScenario = launchActivity()
        Espresso.onView(ViewMatchers.withId(R.id.btn_message))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        activityScenario.close()
    }

    /* TODO fix
    @Test
    fun autocompleteRecyclerView_isDisplayed(){
        val activityScenario = launchActivity()

        val contact = Contact("Bengt Bengtsson", "34567897654")
        runBlocking { contactsRepository.insert(contact) }

        Espresso.onView(ViewMatchers.withId(R.id.et_contact_input))
            .perform(ViewActions.typeText("Bengt"))

        Espresso.onView(ViewMatchers.withId(R.id.rv_autocomplete_list))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        activityScenario.close()
    }

    @Test
    fun autocompleteRecyclerView_chooseItem() {
        val activityScenario = launchActivity()

        val contact = Contact("Bengt Bengtsson", "34567897654")
        runBlocking { contactsRepository.insert(contact) }

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
        activityScenario.close()
    }

    @Test
    fun contactList_writeSpaceAsFirstCharacter_isNotValid(){
        val activityScenario = launchActivity()
        Espresso.onView(ViewMatchers.withId(R.id.et_contact_input))
            .perform(ViewActions.typeText("     "))
            .check(ViewAssertions.matches(ViewMatchers.withText("")))
        activityScenario.close()
    }
     */

    private fun launchActivity(): ActivityScenario<MainActivity> {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        activityScenario.onActivity { activity ->
            val action = DashboardFragmentDirections.actionDashboardFragmentToAddEditTreatmentFragment(title = activity.getString(R.string.add))
            activity.findNavController(R.id.nav_host_fragment).navigate(action)
        }
        return activityScenario
    }


}
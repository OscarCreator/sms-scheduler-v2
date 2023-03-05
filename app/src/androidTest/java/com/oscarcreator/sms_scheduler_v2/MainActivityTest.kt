import android.Manifest
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.oscarcreator.sms_scheduler_v2.MainActivity
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.util.ServiceLocator
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private lateinit var repository: ScheduledTreatmentsRepository

    @Rule
    @JvmField
    val mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.SEND_SMS)

    @Before
    fun init() {

    }

    @After
    fun reset() {
        ServiceLocator.resetRepositories()
    }

    @Test
    fun createScheduledTreatment() {
        launchActivity<MainActivity>().use {
            //navigate to add new scheduled treatment
            onView(withId(R.id.fab_add_treatment)).perform(click())
        }

        //select all values
    }



}
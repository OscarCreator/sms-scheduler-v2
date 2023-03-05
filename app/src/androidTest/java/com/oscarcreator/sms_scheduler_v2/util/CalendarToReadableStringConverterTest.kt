package com.oscarcreator.sms_scheduler_v2.util

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.oscarcreator.sms_scheduler_v2.R
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.util.*

class CalendarToReadableStringConverterTest {

    lateinit var context: Context

    @Before
    fun setup(){
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun dateToText_1Jan2020_returnsWed_1_1(){
        val currentTime = Calendar.getInstance()
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.YEAR, 2020)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.MONTH, 0)
        calendar.set(Calendar.HOUR_OF_DAY, 15)
        calendar.set(Calendar.MINUTE, 22)

        assertThat(calendar.dateToText(context, currentTime, Locale.ENGLISH), `is`("15:22 1/1"))
    }

    @Test
    fun dateToText_1Jan2020Swedish_returns_on_1_1(){
        val currentTime = Calendar.getInstance()
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.YEAR, 2020)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.MONTH, 0)
        calendar.set(Calendar.HOUR_OF_DAY, 13)
        calendar.set(Calendar.MINUTE, 24)

        assertThat(calendar.dateToText(context, currentTime, Locale.forLanguageTag("sv")),
            `is`("13:24 1/1"))
    }

    @Test
    fun dateToText_oneDayDifference_returnsTomorrow(){
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.YEAR, 2020)
        currentTime.set(Calendar.DAY_OF_MONTH, 1)
        currentTime.set(Calendar.MONTH, 0)
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.YEAR, 2020)
        calendar.set(Calendar.DAY_OF_MONTH, 2)
        calendar.set(Calendar.MONTH, 0)
        calendar.set(Calendar.HOUR_OF_DAY, 3)
        calendar.set(Calendar.MINUTE, 33)

        val tomorrowString = context.resources.getString(R.string.tomorrow)

        assertThat(calendar.dateToText(context, currentTime, Locale.ENGLISH), `is`("3:33 $tomorrowString"))
    }


    @Test
    fun dateToText_sameDay_returnsToday(){
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.YEAR, 2020)
        currentTime.set(Calendar.DAY_OF_MONTH, 2)
        currentTime.set(Calendar.MONTH, 0)
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.YEAR, 2020)
        calendar.set(Calendar.DAY_OF_MONTH, 2)
        calendar.set(Calendar.MONTH, 0)
        calendar.set(Calendar.HOUR_OF_DAY, 19)
        calendar.set(Calendar.MINUTE, 1)

        val tomorrowString = context.resources.getString(R.string.today)

        assertThat(calendar.dateToText(context, currentTime, Locale.ENGLISH), `is`("19:01 $tomorrowString"))
    }

    @Test
    fun dateToText_oneDayDifferenceAtEndOfMonth_returnsTomorrow(){
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.YEAR, 2020)
        currentTime.set(Calendar.DAY_OF_MONTH, 31)
        currentTime.set(Calendar.MONTH, 0)
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.YEAR, 2020)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 28)

        val tomorrowString = context.resources.getString(R.string.tomorrow)

        assertThat(calendar.dateToText(context, currentTime, Locale.ENGLISH), `is`("8:28 $tomorrowString"))
    }

}
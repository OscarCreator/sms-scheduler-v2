package com.oscarcreator.pigeon.util

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ReadableStringConverterTester {

    companion object {
        private const val ONE_MINUTE = 60L * 1000L
        private const val ONE_HOUR = ONE_MINUTE * 60L
        private const val ONE_DAY = ONE_HOUR * 24L
    }

    @Test
    fun test_oneMinute_positive1m(){
        val millis = ONE_MINUTE
        assertThat(millis.toTimeTemplateText(), `is`("1m"))
    }

    @Test
    fun test_fourDays_positive4d(){
        val millis = ONE_DAY * 4
        assertThat(millis.toTimeTemplateText(), `is`("4d"))
    }

    @Test
    fun test_sevenHours_positive7h(){
        val millis = ONE_HOUR * 7
        assertThat(millis.toTimeTemplateText(), `is`("7h"))
    }

    @Test
    fun test_threeMinutesAndOneDay_positive1d3m(){
        val millis = ONE_MINUTE * 3 + ONE_DAY
        assertThat(millis.toTimeTemplateText(), `is`("1d 3m"))
    }

    @Test
    fun test_threeMinutesAndOneHour_positive1h3m(){
        val millis = ONE_MINUTE * 3 + ONE_HOUR
        assertThat(millis.toTimeTemplateText(), `is`("1h 3m"))
    }

    @Test
    fun test_oneMinuteOneHourOneDay_positive1d1h1m(){
        val millis = ONE_MINUTE + ONE_HOUR + ONE_DAY
        assertThat(millis.toTimeTemplateText(), `is`("1d 1h 1m"))
    }

    @Test
    fun test_negativeOneDay_negative1d(){
        val millis = - ONE_DAY
        assertThat(millis.toTimeTemplateText(), `is`("-1d"))
    }



}
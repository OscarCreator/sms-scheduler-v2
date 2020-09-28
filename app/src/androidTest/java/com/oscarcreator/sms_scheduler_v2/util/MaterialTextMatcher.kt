package com.oscarcreator.sms_scheduler_v2.util

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import com.google.android.material.textview.MaterialTextView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`

fun withMaterialText(text: String): Matcher<View> {
    return withMaterialText(`is`(text))
}

private fun withMaterialText(textMatcher: Matcher<String>): Matcher<View> {
    return object : BoundedMatcher<View, MaterialTextView>(MaterialTextView::class.java) {
        override fun matchesSafely(view: MaterialTextView): Boolean {
            val text = view.text.toString()
            return textMatcher.matches(text)
        }

        override fun describeTo(description: Description) {
            description.appendText("with text: ")
            textMatcher.describeTo(description)
        }
    }
}



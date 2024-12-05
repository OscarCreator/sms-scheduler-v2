package com.oscarcreator.pigeon.util

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`

fun withMaterialText(text: String): Matcher<View> {
    return withMaterialText(`is`(text))
}

fun withMaterialButtonText(text: String): Matcher<View> {
    return withMaterialButtonText(`is`(text))
}

private fun withMaterialText(textMatcher: Matcher<String>): Matcher<View> {
    return object : BoundedMatcher<View, MaterialTextView>(MaterialTextView::class.java) {
        override fun matchesSafely(view: MaterialTextView): Boolean {
            val text = view.text.toString()
            return textMatcher.matches(text)
        }

        override fun describeTo(description: Description) {
            description.appendText("text=")
            textMatcher.describeTo(description)
        }
    }
}

private fun withMaterialButtonText(textMatcher: Matcher<String>): Matcher<View> {
    return object : BoundedMatcher<View, MaterialButton>(MaterialButton::class.java){
        override fun matchesSafely(item: MaterialButton): Boolean {
            val text = item.text.toString()
            return textMatcher.matches(text)
        }

        override fun describeTo(description: Description) {
            description.appendText("text=")
            textMatcher.describeTo(description)
        }

    }
}

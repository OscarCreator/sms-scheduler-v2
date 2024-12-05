package com.oscarcreator.pigeon.util

import android.text.InputFilter
import android.text.Spanned

class IntInputFilter : InputFilter {
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input = (dest.toString() + source.toString()).toInt()
            if (input <= Int.MAX_VALUE) {
                return null
            }
        } catch (e: NumberFormatException) {}
        return "";
    }


}
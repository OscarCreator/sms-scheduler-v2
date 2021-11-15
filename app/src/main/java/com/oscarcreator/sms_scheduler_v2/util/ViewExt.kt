package com.oscarcreator.sms_scheduler_v2.util

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(anchor: View?, snackbarText: String, timeLength: Int) {
    Snackbar.make(this, snackbarText, timeLength).run {
        addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
            }

            override fun onShown(sb: Snackbar?) {
                super.onShown(sb)
            }
        })
        anchorView = anchor
        show()
    }
}

fun View.setupSnackbar(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<Event<Int>>,
    timeLength: Int,
    anchor: View? = null,
) {
    snackbarEvent.observe(lifecycleOwner, EventObserver {
        showSnackbar(anchor, context.getString(it), timeLength)
    })
}
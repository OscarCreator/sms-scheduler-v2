package com.oscarcreator.sms_scheduler_v2.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        //TODO make sure not to edit scheduledTreatments before this has run

        val scope = MainScope()

        scope.launch {

            val scheduledTreatments = ServiceLocator.provideScheduledTreatmentsRepository(context, scope).getUpcomingScheduledTreatmentsWithData()

            for (scheduledTreatment in scheduledTreatments){
                //reschedule all alarms scheduled before
                scheduleAlarm(context, scheduledTreatment)
            }

            cancel()
        }
    }
} 
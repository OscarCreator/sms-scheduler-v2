package com.oscarcreator.pigeon.util

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.oscarcreator.pigeon.R
import com.oscarcreator.pigeon.addeditmessage.replaceVariables
import com.oscarcreator.pigeon.data.AppDatabase
import com.oscarcreator.pigeon.data.scheduled.SmsStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class DeliveredSmsReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "DeliveredSmsReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {

        val id = intent.getLongExtra("id", -1)

        when (resultCode) {

            Activity.RESULT_OK -> {
                if (id != -1L) {
                    GlobalScope.launch(Dispatchers.IO) {
                        val database = AppDatabase.getDatabase(context, scope = this)

                        val scheduledTreatment = database.scheduledTreatmentDao().getScheduledTreatmentWithData(id)
                        if (scheduledTreatment != null) {
                            scheduledTreatment.scheduledTreatment.smsStatus = SmsStatus.RECEIVED
                            scheduledTreatment.scheduledTreatment.smsDeliveredTime = Calendar.getInstance()

                            when (database.scheduledTreatmentDao().update(scheduledTreatment.scheduledTreatment)) {
                                1 -> Log.i(TAG, "SMS DELIVERED, Scheduled treatment updated (id = $id)")

                                else -> {
                                    // could not update scheduled treatment
                                    Log.e(TAG, "SMS DELIVERED, Scheduled treatment not updated (id = $id)")
                                }
                            }
                        }else {
                            // Alarm not canceled when scheduled treatment was deleted
                            Log.e(TAG, "SMS DELIVERED, Alarm not canceled and scheduled treatment not found (id = $id)")
                        }
                    }

                }else {
                    Log.e(TAG, "SMS DELIVERED, Unknown error (id = -1)")
                }
            }

            else -> {
                if (id != -1L) {
                    GlobalScope.launch {
                        val database = AppDatabase.getDatabase(context, this)
                        val scheduledTreatment = database.scheduledTreatmentDao().getScheduledTreatmentWithData(id)
                        if (scheduledTreatment != null) {
                            scheduledTreatment.scheduledTreatment.smsStatus = SmsStatus.ERROR

                            when (database.scheduledTreatmentDao().update(scheduledTreatment.scheduledTreatment)) {
                                1 -> {
                                    Log.i(TAG, "SMS NOT DELIVERED, Scheduled treatment updated (id = $id, resultcode = $resultCode)")
                                    sendNotificationToScheduledTreatment(context, intent, context.getString(R.string.sms_not_delivered_title, scheduledTreatment.contact.name), scheduledTreatment.replaceVariables(), scheduledTreatment)
                                }

                                else -> {
                                    Log.e(TAG, "SMS NOT DELIVERED, Scheduled treatment not updated (id = $id, resultcode = $resultCode)")
                                }
                            }
                        } else {
                            Log.e(TAG, "SMS NOT DELIVERED, Alarm not canceled and scheduled treatment not found (id = $id, resultcode = $resultCode)")
                        }
                    }
                }else {
                    Log.e(TAG, "SMS NOT DELIVERED, Unknown error (id = -1, resultcode = $resultCode)")
                }
            }
        }
    }
}
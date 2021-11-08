package com.oscarcreator.sms_scheduler_v2.util

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.data.scheduled.SmsStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class DeliveredSmsReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "DeliveredSmsReceiver"
    }

    //TODO change smsstatus to error when not sent

    override fun onReceive(context: Context, intent: Intent) {

        val id = intent.getLongExtra("id", -1)
        val receiver = intent.getStringExtra("customer")

        when (resultCode) {

            Activity.RESULT_OK -> {
                // sms succeeded to be delivered, mark scheduledTreatment in database
                if (id != -1L) {
                    GlobalScope.launch(Dispatchers.IO) {
                        val database = AppDatabase.getDatabase(context, scope = this)

                        val scheduledTreatment = database.scheduledTreatmentDao().getScheduledTreatmentWithData(id)
                        if (scheduledTreatment != null) {
                            scheduledTreatment.scheduledTreatment.smsStatus = SmsStatus.RECEIVED
                            scheduledTreatment.scheduledTreatment.smsDeliveredTime = Calendar.getInstance()

                            when (database.scheduledTreatmentDao().update(scheduledTreatment.scheduledTreatment)) {
                                1 -> Log.i(TAG, "Sms delivered, scheduled treatment updated. ID = $id")

                                else -> {
                                    //TODO unknown error
                                    // should never happen. #1 In app error notification
                                    Log.e(TAG, "Sms delivered, scheduled treatment NOT updated. ID $id")
                                    sendNotificationToScheduledTreatment(context, intent , "Sms delivered, smsstatus NOT updated", "id = $id, receiver = $receiver, updated didn't return 1")
                                }
                            }
                        }else {
                            //TODO unknown error, scheduledTreatment does not exist in database but sms sent
                            // should never happen. #2 Deletion of scheduled treatment should remove broadcast receiver
                            Log.e(TAG, "Unknown error, scheduled treatment with id = $id, not found but sms was successfully delivered.")
                            sendNotificationToScheduledTreatment(context, intent , "Sms delivered, smsstatus NOT updated", "id = $id, receiver = $receiver, scheduled treatment is null")
                        }
                    }

                }else {
                    //TODO unknown error, id = -1. Sms sent but status not able to be changed
                    // should never happen. #1 In app error notification
                    Log.e(TAG, "Unknown error, id = -1 but sms delivered successfully. Not able to change status.")
                    sendNotification(context, intent, "Sms delivered, smsstatus NOT updated, id = -1L", "receiver = $receiver")
                }
            }

            else -> {
                if (id != -1L) {
                    GlobalScope.launch {
                        val database = AppDatabase.getDatabase(context, this)
                        val scheduledTreatment = database.scheduledTreatmentDao().getScheduledTreatmentWithData(id)
                        if (scheduledTreatment != null) {
                            scheduledTreatment.scheduledTreatment.smsStatus = SmsStatus.ERROR

                            //TODO send notification

                            when (database.scheduledTreatmentDao().update(scheduledTreatment.scheduledTreatment)) {
                                1 -> Log.i(TAG, "Sms not delivered, scheduled treatment updated. ID = $id")

                                else -> {
                                    //TODO unknown error
                                    // should never happen. #1 In app error notification
                                    Log.e(TAG, "Sms not delivered, scheduled treatment NOT updated. ID $id")
                                    sendNotificationToScheduledTreatment(context, intent, "Sms not delivered, smsstatus NOT updated", "id = $id, receiver = $receiver")
                                }
                            }

                        } else {
                            //TODO unknown error, scheduledTreatment does not exist in database
                            // should never happen. #2 Deletion of scheduled treatment should remove broadcast receiver
                            Log.e(TAG, "Unknown error, scheduled treatment with id = $id, not found and sms not sent.")
                            sendNotificationToScheduledTreatment(context, intent, "Sms not delivered, smsstatus NOT updated", "id = $id, receiver = $receiver, scheduled treatment = null")
                        }
                    }
                }else {
                    //TODO unknown error, id = -1. Sms not sent but status not able to be changed
                    // should never happen. #1 In app error notification
                    Log.e(TAG, "Unknown error, id = -1 but sms not delivered. Not able to change status.")
                    sendNotification(context, intent, "Sms not delivered, smsstatus NOT updated id = -1L", "receiver = $receiver")
                }
            }
        }

    }
}
package com.oscarcreator.sms_scheduler_v2.util

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager

class SentSmsReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "SendSmsReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {

        when (resultCode) {

            Activity.RESULT_OK -> {

            }

            SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                //TODO extract to strings.xml
                sendNotification(context, intent,
                    "Generic failure. Failed to be sent",
                    "namn: ${intent.getStringExtra("customer")}")
            }

            SmsManager.RESULT_ERROR_NO_SERVICE -> {
                sendNotification(context, intent,
                    "No service. Failed to be sent",
                    "namn: ${intent.getStringExtra("customer")}")
            }

            SmsManager.RESULT_ERROR_NULL_PDU -> {
                sendNotification(context, intent,
                    "No PDU. Failed to be sent",
                    "namn: ${intent.getStringExtra("customer")}")
            }

            SmsManager.RESULT_ERROR_RADIO_OFF -> {
                sendNotification(context, intent,
                    "Radio off. Failed to be sent",
                    "namn: ${intent.getStringExtra("customer")}")
            }

            else -> {
                sendNotification(context, intent,
                    "Unknown error. Failed to be sent",
                    "namn: ${intent.getStringExtra("customer")}, code: $resultCode")
            }
        }

    }
}
package com.oscarcreator.sms_scheduler_v2.util

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.addeditmessage.replaceVariables
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.data.scheduled.SmsStatus
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

@DelicateCoroutinesApi
class SentSmsReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "SendSmsReceiver"
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
                            scheduledTreatment.scheduledTreatment.smsStatus = SmsStatus.SENT
                            scheduledTreatment.scheduledTreatment.smsSentTime = Calendar.getInstance()

                            when (database.scheduledTreatmentDao().update(scheduledTreatment.scheduledTreatment)) {
                                //Everything as normal
                                1 -> Log.i(TAG, "SMS SENT, Scheduled treatment updated (id = $id)")

                                else -> {
                                    Log.e(TAG, "SMS SENT, Scheduled treatment not updated (id = $id)")
                                }
                            }
                        }else {
                            Log.e(TAG, "SMS SENT, Scheduled treatment not updated and not found (id = $id)")
                        }
                    }

                }else {
                    Log.e(TAG, "SMS SENT, Unknown error (id = -1)")
                }
            }

            //SmsManager.RESULT_ERROR_GENERIC_FAILURE
            //SmsManager.RESULT_ERROR_NO_SERVICE
            //SmsManager.RESULT_ERROR_NULL_PDU
            //SmsManager.RESULT_ERROR_RADIO_OFF
            else -> {
                if (id != -1L) {
                    GlobalScope.launch(Dispatchers.IO) {
                        val database = AppDatabase.getDatabase(context, this)
                        val scheduledTreatment = database.scheduledTreatmentDao().getScheduledTreatmentWithData(id)
                        if (scheduledTreatment != null) {
                            scheduledTreatment.scheduledTreatment.smsStatus = SmsStatus.ERROR
                            when (database.scheduledTreatmentDao().update(scheduledTreatment.scheduledTreatment)) {
                                1 -> {
                                    //Everything as normal
                                    Log.i(TAG, "SMS NOT SENT, Scheduled treatment updated (id = $id, resultCode = $resultCode)")
                                    sendNotificationToScheduledTreatment(context, intent, context.getString(R.string.sms_not_sent_title, scheduledTreatment.contact.name), scheduledTreatment.replaceVariables(), scheduledTreatment)
                                }
                                else -> {
                                    Log.e(TAG, "SMS NOT SENT, Scheduled treatment not updated (id = $id, resultCode = $resultCode)")
                                }
                            }
                        } else {
                            Log.e(TAG, "SMS NOT SENT, Scheduled treatment not updated and not found (id = $id, resultCode = $resultCode)")
                        }
                    }
                } else {
                    Log.e(TAG, "SMS NOT SENT, Unknown error (id = -1, resultCode = $resultCode)")
                }
            }
        }
    }
}
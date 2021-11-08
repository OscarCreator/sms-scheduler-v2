package com.oscarcreator.sms_scheduler_v2.util

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.data.scheduled.SmsStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class SentSmsReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "SendSmsReceiver"
    }
    //TODO change smsstatus to error when not sent

    override fun onReceive(context: Context, intent: Intent) {

        val id = intent.getLongExtra("id", -1)
        val receiver = intent.getStringExtra("customer")

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
                                1 -> Log.i(TAG, "Sms sent, scheduled treatment updated. ID = $id")

                                else -> {
                                    //Should Never Happen
                                    //TODO In app error notification
                                    // manual updating, by matching other content
                                    Log.e(TAG, "Sms sent, scheduled treatment NOT updated. ID $id")
                                    sendNotification(context, intent, "Sms sent but not updated", "you need to manually update id = $id, receiver = $receiver")
                                }
                            }
                        }else {
                            //Should Never Happen
                            //TODO In app error notification
                            // manual updating, by matching other content
                            Log.e(TAG, "Unknown error, scheduled treatment with id = $id, not found but sms was successfully sent.")
                            sendNotification(context, intent, "Unknown error id = $id sent but not found in database", "you need to manually update id = $id, receiver = $receiver")
                        }
                    }

                }else {
                    //Should Never Happen
                    //TODO unknown error, id = -1. Sms sent but status not able to be changed
                    // should never happen. #1 In app error notification
                    Log.e(TAG, "Unknown error, id = -1 but sms sent successfully. Not able to change status.")
                    sendNotification(context, intent, "Sms successfully sent, unknown error id = -1", "$receiver")
                }
            }

            SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                if (id != -1L) {
                    GlobalScope.launch(Dispatchers.IO) {
                        val database = AppDatabase.getDatabase(context, this)
                        val scheduledTreatment = database.scheduledTreatmentDao().getScheduledTreatmentWithData(id)
                        if (scheduledTreatment != null) {
                            scheduledTreatment.scheduledTreatment.smsStatus = SmsStatus.ERROR
                            when (database.scheduledTreatmentDao().update(scheduledTreatment.scheduledTreatment)) {
                                1 -> {
                                    //Everything as normal
                                    Log.i(TAG, "Sms not sent: generic failure, smsstatus updated. ID = $id")
                                    sendNotificationToScheduledTreatment(context, intent, "Generic failure sms not sent", "sms with id = $id, receiver = $receiver need to be resent")
                                }
                                else -> {
                                    //Should Never Happen
                                    //TODO In app error notification
                                    // manual updating, by matching other content
                                    Log.e(TAG, "Sms not sent: generic failure, sms NOT updated. ID = $id")
                                    sendNotificationToScheduledTreatment(context, intent, "Generic failure sms not sent", "sms with id = $id, receiver = $receiver need to manually updated")
                                }
                            }
                        } else {
                            //Should Never Happen
                            //TODO In app error notification
                            // manual updating, by matching other content
                            Log.e(TAG, "Unkomn error, id = $id, scheduled treatment not sent and smsstatus NOT updated")
                            sendNotification(context, intent, "Generic failure sms not sent", "sms not found in database id = $id, receiver = $receiver")
                        }
                    }
                } else {
                    //Should Never Happen
                    //TODO In app error notification
                    // manual updating, by matching other content
                    sendNotification(context, intent, "Generic failure. Failed to be sent, id = -1", "receiver = $receiver")
                }



            }

            SmsManager.RESULT_ERROR_NO_SERVICE -> {
                if (id != -1L) {
                    GlobalScope.launch(Dispatchers.IO) {
                        val database = AppDatabase.getDatabase(context, this)
                        val scheduledTreatment = database.scheduledTreatmentDao().getScheduledTreatmentWithData(id)
                        if (scheduledTreatment != null) {
                            scheduledTreatment.scheduledTreatment.smsStatus = SmsStatus.ERROR
                            when (database.scheduledTreatmentDao().update(scheduledTreatment.scheduledTreatment)) {
                                1 -> {
                                    //Everything as normal
                                    Log.i(TAG, "Sms not sent: no service, smsstatus updated. ID = $id")
                                    sendNotificationToScheduledTreatment(context, intent, "No Service sms not sent", "sms with id = $id, receiver = $receiver need to be resent")
                                }
                                else -> {
                                    //Should Never Happen
                                    //TODO In app error notification
                                    // manual updating, by matching other content
                                    Log.e(TAG, "Sms not sent: no servicee, sms NOT updated. ID = $id")
                                    sendNotificationToScheduledTreatment(context, intent, "No service sms not sent", "sms with id = $id, receiver = $receiver need to manually updated")
                                }
                            }
                        } else {
                            //Should Never Happen
                            //TODO In app error notification
                            // manual updating, by matching other content
                            Log.e(TAG, "No Service, Unkomn error, id = $id, scheduled treatment not sent and smsstatus NOT updated")
                            sendNotification(context, intent, "No service sms not sent", "sms not found in database id = $id, receiver = $receiver")
                        }
                    }
                } else {
                    //Should Never Happen
                    //TODO In app error notification
                    // manual updating, by matching other content
                    Log.e(TAG, "No Service, id = -1L, sms not sent, smsstatus NOT updated")
                    sendNotification(context, intent, "No service. Failed to be sent, id = -1", "receiver = $receiver")
                }
            }

            SmsManager.RESULT_ERROR_NULL_PDU -> {
                if (id != -1L) {
                    GlobalScope.launch(Dispatchers.IO) {
                        val database = AppDatabase.getDatabase(context, this)
                        val scheduledTreatment = database.scheduledTreatmentDao().getScheduledTreatmentWithData(id)
                        if (scheduledTreatment != null) {
                            scheduledTreatment.scheduledTreatment.smsStatus = SmsStatus.ERROR
                            when (database.scheduledTreatmentDao().update(scheduledTreatment.scheduledTreatment)) {
                                1 -> {
                                    //Everything as normal
                                    Log.i(TAG, "Sms not sent: NO PDU, smsstatus updated. ID = $id")
                                    sendNotificationToScheduledTreatment(context, intent, "NO PDU sms not sent", "sms with id = $id, receiver = $receiver need to be resent")
                                }
                                else -> {
                                    //Should Never Happen
                                    //TODO In app error notification
                                    // manual updating, by matching other content
                                    Log.e(TAG, "Sms not sent: NO PDU, sms NOT updated. ID = $id")
                                    sendNotificationToScheduledTreatment(context, intent, "NO PDU sms not sent", "sms with id = $id, receiver = $receiver need to manually updated")
                                }
                            }
                        } else {
                            //Should Never Happen
                            //TODO In app error notification
                            // manual updating, by matching other content
                            Log.e(TAG, "NO PDU, Unkomn error, id = $id, scheduled treatment not sent and smsstatus NOT updated")
                            sendNotification(context, intent, "NO PDU sms not sent", "sms not found in database id = $id, receiver = $receiver")
                        }
                    }
                } else {
                    //Should Never Happen
                    //TODO In app error notification
                    // manual updating, by matching other content
                    Log.e(TAG, "NO PDU, id = -1L, sms not sent, smsstatus NOT updated")
                    sendNotification(context, intent, "NO PDU. Failed to be sent, id = -1", "receiver = $receiver")
                }
            }

            SmsManager.RESULT_ERROR_RADIO_OFF -> {
                if (id != -1L) {
                    GlobalScope.launch(Dispatchers.IO) {
                        val database = AppDatabase.getDatabase(context, this)
                        val scheduledTreatment = database.scheduledTreatmentDao().getScheduledTreatmentWithData(id)
                        if (scheduledTreatment != null) {
                            scheduledTreatment.scheduledTreatment.smsStatus = SmsStatus.ERROR
                            when (database.scheduledTreatmentDao().update(scheduledTreatment.scheduledTreatment)) {
                                1 -> {
                                    //Everything as normal
                                    Log.i(TAG, "Sms not sent: Radio off, smsstatus updated. ID = $id")
                                    sendNotificationToScheduledTreatment(context, intent, "Radio off sms not sent", "sms with id = $id, receiver = $receiver need to be resent")
                                }
                                else -> {
                                    //Should Never Happen
                                    //TODO In app error notification
                                    // manual updating, by matching other content
                                    Log.e(TAG, "Sms not sent: Radio off, sms NOT updated. ID = $id")
                                    sendNotificationToScheduledTreatment(context, intent, "Radio off sms not sent", "sms with id = $id, receiver = $receiver need to manually updated")
                                }
                            }
                        } else {
                            //Should Never Happen
                            //TODO In app error notification
                            // manual updating, by matching other content
                            Log.e(TAG, "Radio off, Unkomn error, id = $id, scheduled treatment not sent and smsstatus NOT updated")
                            sendNotification(context, intent, "Radio off sms not sent", "sms not found in database id = $id, receiver = $receiver")
                        }
                    }
                } else {
                    //Should Never Happen
                    //TODO In app error notification
                    // manual updating, by matching other content
                    Log.e(TAG, "Radio off, id = -1L, sms not sent, smsstatus NOT updated")
                    sendNotification(context, intent, "Radio off. Failed to be sent, id = -1", "receiver = $receiver")
                }
            }

            else -> {
                sendNotification(context, intent,
                    "Unknown error. Failed to be sent",
                    "id = $id, namn = $receiver, resultCode: $resultCode")
            }
        }

    }
}
package com.oscarcreator.sms_scheduler_v2.util

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import com.oscarcreator.sms_scheduler_v2.MainActivity
import com.oscarcreator.sms_scheduler_v2.NotificationConstants
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.addeditmessage.replaceVariables
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageTimeTemplateAndContact
import com.oscarcreator.sms_scheduler_v2.data.scheduled.SmsStatus
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@DelicateCoroutinesApi
class ScheduleSmsReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "ScheduleSmsReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra("id", -1)

        // cancel current notification if present, only used when send now button pressed
        with(NotificationManagerCompat.from(context)) {
            cancel(id.toInt())
        }

        if (id != -1L) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                sendSms(context, intent)
            }else{
                // mark error
                GlobalScope.launch(Dispatchers.IO) {
                    val database = AppDatabase.getDatabase(context, this)
                    val scheduledTreatment = database.scheduledTreatmentDao().getScheduledTreatmentWithData(id)
                    if (scheduledTreatment != null) {
                        scheduledTreatment.scheduledTreatment.smsStatus = SmsStatus.ERROR

                        when (database.scheduledTreatmentDao().update(scheduledTreatment.scheduledTreatment)) {
                            1 -> {
                                Log.i(TAG,"Permission denied, Scheduled treatment updated (id = $id)")
                                sendNotification(context, intent,
                                    context.getString(R.string.permission_not_granted_title),
                                    context.getString(R.string.permission_not_granted_description))

                                sendNotificationToScheduledTreatment(context, intent,
                                    context.getString(R.string.sms_not_sent_title, scheduledTreatment.contact.name),
                                    scheduledTreatment.replaceVariables(),
                                    scheduledTreatment
                                )
                            }
                            else -> {
                                Log.e(TAG, "Permission denied, Scheduled treatment not updated (id = $id)")
                            }
                        }
                    } else {
                        // scheduled treatment not found
                        Log.e(TAG, "Permission denied, Scheduled treatment not found (id = $id)")
                    }
                }
            }
        } else {
            Log.e(TAG, "Unknown error, Alarm received but sms not sent (id = -1)")
        }

    }

}

fun scheduleAlarm(context: Context, scheduledTreatment: ScheduledTreatmentWithMessageTimeTemplateAndContact) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = getScheduleAlarmPendingIntent(context, scheduledTreatment)

    val time = scheduledTreatment.scheduledTreatment.treatmentTime.timeInMillis + scheduledTreatment.timeTemplate.delay
    Log.d("ScheduleSmsReceiver", "scheduleAlarm")
    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
}

fun deleteAlarm(context: Context, scheduledTreatment: ScheduledTreatmentWithMessageTimeTemplateAndContact) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val pendingIntent = getScheduleAlarmPendingIntent(context, scheduledTreatment)

    alarmManager.cancel(pendingIntent)
}

fun sendSmsNow(context: Context, scheduledTreatment: ScheduledTreatmentWithMessageTimeTemplateAndContact) {
    val intent = Intent(context, ScheduleSmsReceiver::class.java)
    intent.putExtra("customer", scheduledTreatment.contact.name)
    intent.putExtra("phone_num", scheduledTreatment.contact.phoneNumber)
    intent.putExtra("id", scheduledTreatment.scheduledTreatment.scheduledTreatmentId)
    intent.putExtra("message", scheduledTreatment.replaceVariables())

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
        sendSms(context, intent)
    } else {
        sendNotification(context, intent,
            context.getString(R.string.permission_not_granted_title),
            context.getString(R.string.permission_not_granted_description))
        sendNotificationToScheduledTreatment(context, intent,
            context.getString(R.string.sms_not_sent_title, scheduledTreatment.contact.name),
            scheduledTreatment.replaceVariables(),
            scheduledTreatment
        )
    }
}

private fun getScheduleAlarmPendingIntent(context: Context, scheduledTreatment: ScheduledTreatmentWithMessageTimeTemplateAndContact): PendingIntent {

    val intent = Intent(context, ScheduleSmsReceiver::class.java)
    intent.putExtra("customer", scheduledTreatment.contact.name)
    intent.putExtra("phone_num", scheduledTreatment.contact.phoneNumber)
    intent.putExtra("id", scheduledTreatment.scheduledTreatment.scheduledTreatmentId)
    intent.putExtra("message", scheduledTreatment.replaceVariables())
    return  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getBroadcast(
            context,
            scheduledTreatment.scheduledTreatment.scheduledTreatmentId.toInt() * 4,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
    } else {
        PendingIntent.getBroadcast(
            context,
            scheduledTreatment.scheduledTreatment.scheduledTreatmentId.toInt() * 4,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT)
    }
}

private fun sendSms(context: Context, intent: Intent) {
    val phoneNumber = intent.getStringExtra("phone_num")
    val id = intent.getLongExtra("id", -1)
    val message = intent.getStringExtra("message")

    val smsManager = SmsManager.getDefault()

    val messageParts = smsManager.divideMessage(message)

    val sentPendingIntents = arrayListOf<PendingIntent>()
    val deliveredPendingIntents = arrayListOf<PendingIntent>()

    for ((index, messagePart) in messageParts.withIndex()) {
        sentPendingIntents.add(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                context,
                id.toInt() * 4 + 1,
                Intent(context, SentSmsReceiver::class.java).apply { putExtras(intent.extras!!) },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(
                context,
                id.toInt() * 4 + 1,
                Intent(context, SentSmsReceiver::class.java).apply { putExtras(intent.extras!!) },
                PendingIntent.FLAG_UPDATE_CURRENT)
        })

        deliveredPendingIntents.add(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                context,
                id.toInt() * 4 + 2,
                Intent(context, DeliveredSmsReceiver::class.java).apply { putExtras(intent.extras!!) },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                context,
                id.toInt() * 4 + 2,
                Intent(context, DeliveredSmsReceiver::class.java).apply { putExtras(intent.extras!!) },
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        })
    }

    smsManager.sendMultipartTextMessage(phoneNumber, null, messageParts, sentPendingIntents, deliveredPendingIntents)
}

fun sendNotification(context: Context, intent: Intent, title: String, text: String) {

    val id = intent.getLongExtra("id", -1).toInt()

    val builder = NotificationCompat.Builder(context, NotificationConstants.SMS_ERROR_CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSmallIcon(R.drawable.ic_icon_notification)
        .setContentIntent(
            PendingIntent.getActivity(
                context,
                Integer.MAX_VALUE - id,
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                },
                PendingIntent.FLAG_IMMUTABLE
            )
        ).setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notify(Integer.MAX_VALUE - id, builder.build())
    }
}

fun sendNotificationToScheduledTreatment(context: Context, intent: Intent, title: String, text: String, scheduledTreatment: ScheduledTreatmentWithMessageTimeTemplateAndContact) {

    val id = intent.getLongExtra("id", -1)

    val builder = NotificationCompat.Builder(context, NotificationConstants.SMS_ERROR_CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSmallIcon(R.drawable.ic_icon_notification)
        .addAction(R.drawable.ic_send, context.getString(R.string.send_now).uppercase(), getScheduleAlarmPendingIntent(context, scheduledTreatment))
        .setContentIntent(
            NavDeepLinkBuilder(context)

                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.scheduledTreatmentDetailFragment)
                .setArguments(Bundle().apply {
                    putLong("scheduledTreatmentId", id)
                })
                .createTaskStackBuilder().getPendingIntent(id.toInt(), PendingIntent.FLAG_IMMUTABLE)
        ).setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notify(id.toInt(), builder.build())
    }
}


fun removeNotification(context: Context, id: Long) {
    with(NotificationManagerCompat.from(context)) {
        cancel(id.toInt())
    }
}
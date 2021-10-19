package com.oscarcreator.sms_scheduler_v2.util

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import com.oscarcreator.sms_scheduler_v2.MainActivity
import com.oscarcreator.sms_scheduler_v2.NotificationConstants
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.addeditmessage.replaceVariables
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers

class ScheduleSmsReceiver() : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val phoneNumber = intent.getStringExtra("phone_num")
        val id = intent.getLongExtra("id", -1)
        val message = intent.getStringExtra("message")

        if (id != -1L) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                val smsManager = SmsManager.getDefault()
                //TODO format text with data. ex. Name of customer [namn]
                val sentPendingIntent = PendingIntent.getBroadcast(
                    context,
                    id.toInt() * 4 + 1,
                    Intent(context, SentSmsReceiver::class.java).apply { putExtras(intent.extras!!) },
                    PendingIntent.FLAG_UPDATE_CURRENT)

                val deliveredPendingIntent = PendingIntent.getBroadcast(
                    context,
                    id.toInt() * 4 + 2,
                    Intent(context, DeliveredSmsReceiver::class.java).apply { putExtras(intent.extras!!) },
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                smsManager.sendTextMessage(phoneNumber, null, message, sentPendingIntent, deliveredPendingIntent)
            }else{
                sendNotification(context, intent,
                    "Permission send sms not granted",
                    "id: $id, phoneNum: $phoneNumber, message: $message")
            }
        } else {
            sendNotification(context, intent,
                "Unknown error. Id not received",
                "id: $id, phoneNum: $phoneNumber, message: $message")
        }

    }

}

fun scheduleAlarm(context: Context, scheduledTreatment: ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ScheduleSmsReceiver::class.java)
    //TODO "extend" Bundle to make a custom
    intent.putExtra("customer", scheduledTreatment.customers[0].name)
    intent.putExtra("phone_num", scheduledTreatment.customers[0].phoneNumber)
    intent.putExtra("id", scheduledTreatment.scheduledTreatment.id)
    intent.putExtra("message", scheduledTreatment.replaceVariables())
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        scheduledTreatment.scheduledTreatment.id.toInt() * 4,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT)

    val time = scheduledTreatment.scheduledTreatment.treatmentTime.timeInMillis + scheduledTreatment.timeTemplate.delay
    Log.d("ScheduleSmsReceiver", "scheduleAlarm")
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
}

fun deleteAlarm(context: Context, scheduledTreatment: ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ScheduleSmsReceiver::class.java)
    //TODO "extend" Bundle to make a custom
    intent.putExtra("customer", scheduledTreatment.customers[0].name)
    intent.putExtra("phone_num", scheduledTreatment.customers[0].phoneNumber)
    intent.putExtra("id", scheduledTreatment.scheduledTreatment.id)
    intent.putExtra("message", scheduledTreatment.replaceVariables())

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        scheduledTreatment.scheduledTreatment.id.toInt() * 4,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    alarmManager.cancel(pendingIntent)
}

//TODO action in app for failed scheduled sms, so they can be sent manually
fun sendNotification(context: Context, intent: Intent, title: String, text: String) {

    val id = intent.getLongExtra("id", -1).toInt()

    val builder = NotificationCompat.Builder(context, NotificationConstants.SMS_ERROR_CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSmallIcon(R.drawable.ic_contacts)
        .setContentIntent(
            PendingIntent.getActivity(
                context,
                id,
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                },
                0
            )
        ).setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        notify(id, builder.build())
    }
}
//TODO change icon
fun sendNotificationToScheduledTreatment(context: Context, intent: Intent, title: String, text: String) {

    val id = intent.getLongExtra("id", -1)

    val builder = NotificationCompat.Builder(context, NotificationConstants.SMS_ERROR_CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSmallIcon(R.drawable.ic_contacts)
        .setContentIntent(
            NavDeepLinkBuilder(context)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.scheduledTreatmentDetailFragment)
                .setArguments(Bundle().apply {
                    putLong("scheduledTreatmentId", id)
                })
                .createPendingIntent()
        ).setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        notify(id.toInt(), builder.build())
    }
}

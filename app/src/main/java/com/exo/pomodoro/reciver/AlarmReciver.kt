package com.exo.pomodoro.reciver

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.CountDownTimer
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.exo.pomodoro.FeedActivity
import com.exo.pomodoro.R


const val notificationID = 1
const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

const val MY_URI = "https://Pomodoro-app.com"
const val MY_ARGS = "message"
class AlarmReciver  : BroadcastReceiver() {
    private var mp: MediaPlayer? = null

    override fun onReceive(context: Context, intent: Intent)
    {
        println("time is tregerd")
        // Activity Intent
        val intent = Intent(context, FeedActivity::class.java).apply {
            // Note: Add flag(s) for Activity here
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        // Pending Intent
        val pendingIntent = PendingIntent.getActivity(
            context,
            // Note: Use a non-zero positive Integer here, it's good practice ;-)
            System.currentTimeMillis().toInt(),
            intent,
            // Note: Add flag(s) for Pending Intent here
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_play)
            .setContentTitle("Asolar Notification")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentText("Please Orient the oven and  Adjust the lens")
            .setAutoCancel(true)
            .setContentIntent( pendingIntent)
            .build()

        val  manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
        fun stopMedia(){
            mp?.stop()
        }
    }

}
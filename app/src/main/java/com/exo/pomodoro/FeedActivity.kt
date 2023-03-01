package com.exo.pomodoro


import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.exo.pomodoro.databinding.ActivityFeedBinding
import com.exo.pomodoro.reciver.*

import java.time.Instant



class FeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedBinding

    private var cokingMinute: Int? = null
    private var cokingTimeLeft: Long? = null
    private var roundCount: Int? = null
    private var soundTImer: CountDownTimer? = null
    private var restTimer: CountDownTimer? = null
    private var cokingTimer: CountDownTimer? = null

    private var mRound = 1

    private var isCocking = true
    private var isFirst = true

    private var isStop = false

    private var isResetFinished = false

    private var StopTimer = false


    private var mp: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Receive Extras
        cokingMinute = intent.getIntExtra("study", 0) * 60 * 1000
        roundCount = intent.getIntExtra("round", 0)


        // Set Rounds Text
        binding.tvRound.text = "$mRound/$roundCount"
        //Start Timer
        //setRestTimer()
        setupCockingView()
        // Reset Button
        binding.ivReset.setOnClickListener {
            resetOrStart()
        }
        binding.ivStop.setOnClickListener {
            StopTimer = !StopTimer
            if(!isResetFinished) {
                StopStart()
            }else{
                StopStartCocking()
            }




        }

    }




/*
    private fun scheduleNotification()
    {
        val intent = Intent(applicationContext, AlarmReciver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        pendingIntent

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = Instant.now().plusSeconds(0).toEpochMilli()
        println("schedule is runigj")
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            0,
            pendingIntent
        )



    }

 */

// counter pour reset time
private fun MyCounterRest(startFrom : Long){
    isResetFinished = false
    restTimer = object : CountDownTimer(startFrom,1000) {
        override fun onTick(p0: Long) {

            binding.progressBar.progress = (p0 / 1000).toInt()
            binding.tvTimer.text = (p0 / 1000).toString()


        }
        override fun onFinish() {
            mp?.stop()
            isResetFinished = true
            if (isCocking){
                setupCockingView()
            }else{
                // setupBreakView()
                playSound()
                clearAttribute()

            }
        }
    }.start()
}// counter pour coking time
private fun MyCounterCoking(startFrom : Long){

    cokingTimer = object : CountDownTimer(startFrom,1000) {
        override fun onTick(p0: Long) {
            if(createTimeLabels((p0 / 1000).toInt()) == "00:01"){
               // scheduleNotification()
            }
            cokingTimeLeft = p0 / 1000
            binding.progressBar.progress = (p0 /1000).toInt()
            binding.tvTimer.text = createTimeLabels((p0 / 1000).toInt())

        }
        override fun onFinish() {
            Notify()
            soundCount()
            if(mRound < roundCount!!){
                isCocking = true
                cokingMinute = intent.getIntExtra("study", 0) * 60 * 1000
                setRestTimer()
                mRound++
            }else{
                soundCount()

                clearAttribute()

            }
        }
    }.start()
}

    // Set Rest Timer
    @RequiresApi(Build.VERSION_CODES.O)

    private fun setRestTimer(){


        binding.progressBar.progress = 0
        binding.progressBar.max = 10
        MyCounterRest(10500)

    }
    // To stop & start new timer check not null of timer instance first then cancel existing timer & start new one


    // Set Study Timer
    private fun setCkokingTimer(){

        MyCounterCoking(cokingMinute!!.toLong() + 500)
    }



    // Prepare Screen for Cocking Timer
    private fun setupCockingView() {
        binding.tvRound.text = "$mRound/$roundCount"
        binding.progressBar.max = cokingMinute!!/1000

        if (cokingTimer != null)
            cokingTimer = null

        setCkokingTimer()
    }
    private fun soundCount(){
        playSound()
        soundTImer = object : CountDownTimer(10*1000,1000) {
            override fun onTick(p0: Long) {




            }
            override fun onFinish() {
                mp?.stop()
            }
        }.start()
    }
    // Initialize sound file to MediaPlayer
    private fun playSound() {

        try {
            val soundUrl = Uri.parse("android.resource://com.exo.pomodoro/" + R.raw.count_downer)
            mp = MediaPlayer.create(this,soundUrl)
            mp?.isLooping = false
            mp?.start()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }
    // Rest Whole Attributes in FeedActivity
    private fun clearAttribute() {

        binding.ivStop.setImageResource(R.drawable.ic_play)
        binding.progressBar.progress = 0
        binding.tvTimer.text = "0"
        mRound = 1
        binding.tvRound.text = "$mRound/$roundCount"
        restTimer?.cancel()
        cokingTimer?.cancel()
        mp?.reset()
        isStop = true
    }
    // Convert Received Numbers to Minutes and Seconds
    private fun createTimeLabels(time : Int): String {
        var timeLabel = ""
        val minutes = time / 60
        val secends = time % 60

        if (minutes < 10) timeLabel += "0"
        timeLabel += "$minutes:"

        if (secends < 10) timeLabel += "0"
        timeLabel += secends

        return timeLabel
    }
    // For Reset or Restart Pomodoro
    private fun resetOrStart() {
        restTimer?.cancel()
        cokingTimer?.cancel()
        cokingTimeLeft = 20*60
        println("$cokingTimeLeft")
        mp?.reset()
        clearAttribute()
    }



    private fun StopStart(){

            if(StopTimer){
                restTimer!!.cancel()
               println(" hello achraf ${binding.tvTimer.text}")
                binding.ivStop.setImageResource(R.drawable.ic_play)
            }else{
                val startFrom = binding.tvTimer.text.toString().toLong()
                println(" $startFrom")
                MyCounterRest(startFrom * 1000)
                binding.ivStop.setImageResource(R.drawable.vector__2_)
            }
    }
    private fun StopStartCocking(){
        cokingTimer!!.cancel()
            if(StopTimer){
               println(" hello achraf ${binding.tvTimer.text}")
                binding.ivStop.setImageResource(R.drawable.ic_play)
            }else{
                println(" $cokingTimeLeft")
                println(" $cokingMinute")
                cokingTimeLeft?.let { MyCounterCoking(it * 1000) }
                binding.ivStop.setImageResource(R.drawable.vector__2_)
            }
    }
    // Clear Everything When App Destroyed
    override fun onDestroy() {
        super.onDestroy()
        restTimer?.cancel()
        cokingTimer?.cancel()
        mp?.reset()
    }


    private fun createNotificationChannel()
    {
        val name = "Asolar Channel"
        val desc = "Asolaar alarm"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    private fun Notify(){
        // Activity Intent
        val intent = Intent(this, FeedActivity::class.java).apply {
            // Note: Add flag(s) for Activity here
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        // Pending Intent
        val pendingIntent = PendingIntent.getActivity(
            this,
            // Note: Use a non-zero positive Integer here, it's good practice ;-)
            System.currentTimeMillis().toInt(),
            intent,
            // Note: Add flag(s) for Pending Intent here
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.ic_play)
            .setContentTitle("Asolar Notification")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentText("Please Orient the oven and  Adjust the lens")
            .setAutoCancel(true)
            .setContentIntent( pendingIntent)
            .build()

        val  manager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }

}
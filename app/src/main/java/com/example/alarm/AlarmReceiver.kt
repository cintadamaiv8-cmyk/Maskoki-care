package com.example.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.FeedingHistory
import com.example.data.MaskokiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val scheduleId = intent.getIntExtra("SCHEDULE_ID", -1)
        val scheduleTime = intent.getStringExtra("SCHEDULE_TIME") ?: "00:00"
        val foodType = intent.getStringExtra("FOOD_TYPE") ?: "Pakan"

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("FEEDING_CHANNEL", "Jadwal Pakan", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        when (action) {
            "ACTION_FEEDING_ALARM" -> {
                val mainIntent = Intent(context, MainActivity::class.java)
                val mainPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE)

                val feedIntent = Intent(context, AlarmReceiver::class.java).apply {
                    this.action = "ACTION_MARK_FED"
                    putExtra("SCHEDULE_ID", scheduleId)
                    putExtra("SCHEDULE_TIME", scheduleTime)
                    putExtra("FOOD_TYPE", foodType)
                }
                val feedPendingIntent = PendingIntent.getBroadcast(context, scheduleId + 1000, feedIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

                val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
                    this.action = "ACTION_SNOOZE"
                    putExtra("SCHEDULE_ID", scheduleId)
                    putExtra("SCHEDULE_TIME", scheduleTime)
                    putExtra("FOOD_TYPE", foodType)
                }
                val snoozePendingIntent = PendingIntent.getBroadcast(context, scheduleId + 2000, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

                val notification = NotificationCompat.Builder(context, "FEEDING_CHANNEL")
                    .setSmallIcon(android.R.drawable.ic_notification_overlay)
                    .setContentTitle("Waktunya Pakan Maskoki!")
                    .setContentText("Jadwal: \$scheduleTime - \$foodType")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setContentIntent(mainPendingIntent)
                    .addAction(android.R.drawable.ic_menu_save, "PAKAN", feedPendingIntent)
                    .addAction(android.R.drawable.ic_menu_recent_history, "TUNDA", snoozePendingIntent)
                    .build()

                notificationManager.notify(scheduleId, notification)

                // Reschedule if frequency is Harian or Mingguan
                CoroutineScope(Dispatchers.IO).launch {
                    val repo = MaskokiRepository.getInstance(context.applicationContext)
                    val schedules = repo.allFeedingSchedules.first()
                    val schedule = schedules.find { it.id == scheduleId }
                    if (schedule != null) {
                        if (schedule.frequency == "Harian" || schedule.frequency == "Mingguan") {
                            val scheduler = AlarmScheduler(context)
                            scheduler.scheduleFeedingAlarm(schedule)
                        }
                    }
                }
            }
            "ACTION_MARK_FED" -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val repo = MaskokiRepository.getInstance(context.applicationContext)
                    repo.insertFeedingHistory(
                        FeedingHistory(
                            scheduleId = scheduleId,
                            timestamp = System.currentTimeMillis(),
                            scheduleName = "Jadwal \$scheduleTime",
                            foodType = foodType,
                            status = "Berhasil Diberikan"
                        )
                    )
                }
                notificationManager.cancel(scheduleId)
            }
            "ACTION_SNOOZE" -> {
                val scheduler = AlarmScheduler(context)
                scheduler.scheduleSnooze(scheduleId, scheduleTime, foodType)
                notificationManager.cancel(scheduleId)
            }
        }
    }
}

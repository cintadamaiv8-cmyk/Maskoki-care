package com.example.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.data.FeedingSchedule
import java.util.Calendar

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleFeedingAlarm(schedule: FeedingSchedule) {
        val parts = schedule.time.split(":")
        if (parts.size != 2) return
        val hour = parts[0].toIntOrNull() ?: return
        val minute = parts[1].toIntOrNull() ?: return

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            if (schedule.frequency == "Harian") {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            } else if (schedule.frequency == "Mingguan") {
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "ACTION_FEEDING_ALARM"
            putExtra("SCHEDULE_ID", schedule.id)
            putExtra("SCHEDULE_TIME", schedule.time)
            putExtra("FOOD_TYPE", schedule.foodType)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            schedule.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Log security exception
        }
    }

    fun cancelFeedingAlarm(scheduleId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "ACTION_FEEDING_ALARM"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            scheduleId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun scheduleSnooze(scheduleId: Int, scheduleTime: String, foodType: String) {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 10)
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "ACTION_FEEDING_ALARM"
            putExtra("SCHEDULE_ID", scheduleId)
            putExtra("SCHEDULE_TIME", scheduleTime)
            putExtra("FOOD_TYPE", foodType)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            scheduleId + 10000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) { }
    }
}

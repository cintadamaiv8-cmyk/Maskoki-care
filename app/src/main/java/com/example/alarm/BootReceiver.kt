package com.example.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.MaskokiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val scheduler = AlarmScheduler(context)
            val repo = MaskokiRepository.getInstance(context.applicationContext)
            CoroutineScope(Dispatchers.IO).launch {
                val schedules = repo.allFeedingSchedules.first()
                schedules.forEach { schedule ->
                    scheduler.scheduleFeedingAlarm(schedule)
                }
            }
        }
    }
}

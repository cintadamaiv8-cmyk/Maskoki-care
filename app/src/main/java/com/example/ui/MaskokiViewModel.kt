package com.example.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.FileOutputStream

class MaskokiViewModel(private val repository: MaskokiRepository, private val context: Context) : ViewModel() {

    private val alarmScheduler = com.example.alarm.AlarmScheduler(context)

    val userName: StateFlow<String> = repository.userName
    val dashboardImageUri: StateFlow<String?> = repository.dashboardImageUri

    val feedingSchedules = repository.allFeedingSchedules.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val feedingHistory = repository.allFeedingHistory.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val waterChanges = repository.allWaterChanges.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val maintenances = repository.allMaintenances.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Feeding
    fun addFeedingSchedule(time: String, frequency: String, foodType: String) {
        viewModelScope.launch {
            val schedule = FeedingSchedule(time = time, frequency = frequency, foodType = foodType)
            val id = repository.insertFeedingScheduleReturnId(schedule)
            alarmScheduler.scheduleFeedingAlarm(schedule.copy(id = id.toInt()))
        }
    }
    fun deleteFeedingSchedule(schedule: FeedingSchedule) {
        viewModelScope.launch { 
            repository.deleteFeedingSchedule(schedule) 
            alarmScheduler.cancelFeedingAlarm(schedule.id)
        }
    }
    fun markFed(scheduleId: Int, scheduleName: String = "Pakan Terjadwal", foodType: String = "") {
        viewModelScope.launch {
            repository.insertFeedingHistory(
                FeedingHistory(
                    scheduleId = scheduleId, 
                    timestamp = System.currentTimeMillis(),
                    scheduleName = scheduleName,
                    foodType = foodType
                )
            )
        }
    }
    fun addManualFeeding(foodType: String = "Pakan Manual") {
        viewModelScope.launch {
            repository.insertFeedingHistory(
                FeedingHistory(
                    scheduleId = -1, 
                    timestamp = System.currentTimeMillis(),
                    scheduleName = "Pakan Darurat/Cepat",
                    foodType = foodType
                )
            )
        }
    }
    fun clearFeedingHistory() {
        viewModelScope.launch { repository.clearFeedingHistory() }
    }
    fun deleteFeedingHistory(history: FeedingHistory) {
        viewModelScope.launch { repository.deleteFeedingHistory(history) }
    }

    // Water Change
    fun addWaterChange(scheduledTime: Long, percentage: Int, notes: String) {
        viewModelScope.launch {
            repository.insertWaterChange(WaterChange(scheduledTime = scheduledTime, percentage = percentage, notes = notes))
        }
    }
    fun updateWaterChangeStatus(change: WaterChange, completed: Boolean) {
        viewModelScope.launch {
            repository.insertWaterChange(change.copy(isCompleted = completed))
        }
    }
    fun deleteWaterChange(change: WaterChange) {
        viewModelScope.launch { repository.deleteWaterChange(change) }
    }

    // Maintenance
    fun addMaintenance(scheduledTime: Long) {
        viewModelScope.launch {
            repository.insertMaintenance(Maintenance(scheduledTime = scheduledTime))
        }
    }
    fun updateMaintenanceStatus(maintenance: Maintenance, filter: Boolean, glass: Boolean, substrate: Boolean, fish: Boolean) {
        val completed = filter && glass && substrate && fish
        viewModelScope.launch {
            repository.insertMaintenance(maintenance.copy(
                isFilterClean = filter,
                isGlassClean = glass,
                isSubstrateClean = substrate,
                isFishOk = fish,
                isCompleted = completed
            ))
        }
    }
    fun deleteMaintenance(maintenance: Maintenance) {
        viewModelScope.launch { repository.deleteMaintenance(maintenance) }
    }

    // Settings
    fun updateUserName(name: String) {
        repository.updateUserName(name)
    }
    fun updateDashboardImage(uri: String?) {
        repository.updateDashboardImageUri(uri)
    }
    fun saveDashboardImageFromUri(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.filesDir, "dashboard_bg.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                repository.updateDashboardImageUri("file://" + file.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun resetAllData() {
        viewModelScope.launch { repository.wipeAllData() }
    }
}

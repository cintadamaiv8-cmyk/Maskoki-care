package com.example.data

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

class MaskokiRepository(context: Context) {

    // Setup Room DB
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "maskoki_database"
    ).fallbackToDestructiveMigration().build()

    private val dao = db.maskokiDao()

    // Setup SharedPreferences
    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("maskoki_settings", Context.MODE_PRIVATE)

    // Flows for SharedPreferences
    private val _userName = MutableStateFlow(sharedPrefs.getString("user_name", "Pecinta Maskoki") ?: "Pecinta Maskoki")
    val userName: StateFlow<String> = _userName

    private val _dashboardImageUri = MutableStateFlow(sharedPrefs.getString("dashboard_image_uri", null))
    val dashboardImageUri: StateFlow<String?> = _dashboardImageUri

    fun updateUserName(name: String) {
        sharedPrefs.edit().putString("user_name", name).apply()
        _userName.value = name
    }

    fun updateDashboardImageUri(uri: String?) {
        sharedPrefs.edit().putString("dashboard_image_uri", uri).apply()
        _dashboardImageUri.value = uri
    }

    // Room DB access - Feeding
    val allFeedingSchedules: Flow<List<FeedingSchedule>> = dao.getAllFeedingSchedules()
    val allFeedingHistory: Flow<List<FeedingHistory>> = dao.getFeedingHistory()

    suspend fun clearFeedingHistory() = dao.wipeFeedingHistory()
    suspend fun deleteFeedingHistory(history: FeedingHistory) = dao.deleteFeedingHistory(history)

    suspend fun insertFeedingScheduleReturnId(schedule: FeedingSchedule): Long = dao.insertFeedingSchedule(schedule)
    suspend fun insertFeedingSchedule(schedule: FeedingSchedule) { dao.insertFeedingSchedule(schedule) }
    suspend fun deleteFeedingSchedule(schedule: FeedingSchedule) = dao.deleteFeedingSchedule(schedule)
    suspend fun insertFeedingHistory(history: FeedingHistory) {
        dao.insertFeedingHistory(history)
        // Update the lastFedTime in the schedule
        val schedules = dao.getAllFeedingSchedules().first()
        val schedule = schedules.find { it.id == history.scheduleId }
        schedule?.let { 
            dao.insertFeedingSchedule(it.copy(lastFedTime = history.timestamp))
        }
    }

    // Room DB access - Water Change
    val allWaterChanges: Flow<List<WaterChange>> = dao.getAllWaterChanges()
    suspend fun insertWaterChange(change: WaterChange) = dao.insertWaterChange(change)
    suspend fun deleteWaterChange(change: WaterChange) = dao.deleteWaterChange(change)

    // Room DB access - Maintenance
    val allMaintenances: Flow<List<Maintenance>> = dao.getAllMaintenances()
    suspend fun insertMaintenance(maintenance: Maintenance) = dao.insertMaintenance(maintenance)
    suspend fun deleteMaintenance(maintenance: Maintenance) = dao.deleteMaintenance(maintenance)

    // Reset Data
    suspend fun wipeAllData() {
        dao.wipeFeedingSchedule()
        dao.wipeFeedingHistory()
        dao.wipeWaterChange()
        dao.wipeMaintenance()
        // DO NOT wipe user settings as they are separate, but if needed we can clear prefs:
        // sharedPrefs.edit().clear().apply()
        // _userName.value = "Pecinta Maskoki"
        // _dashboardImageUri.value = null
    }

    companion object {
        @Volatile
        private var INSTANCE: MaskokiRepository? = null

        fun getInstance(context: Context): MaskokiRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = MaskokiRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}

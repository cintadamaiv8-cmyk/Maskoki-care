package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// Entitas Data (Tabel)
@Entity(tableName = "feeding_schedule")
data class FeedingSchedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val time: String, // format HH:mm
    val frequency: String, // "Sekali", "Harian", "Mingguan"
    val foodType: String,
    val lastFedTime: Long = 0L // Timestamp pemberian makan terakhir
)

@Entity(tableName = "feeding_history")
data class FeedingHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val scheduleId: Int,
    val timestamp: Long,
    val scheduleName: String = "Pakan Manual",
    val foodType: String = "",
    val status: String = "Berhasil Diberikan"
)

@Entity(tableName = "water_change")
data class WaterChange(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val scheduledTime: Long,
    val percentage: Int,
    val notes: String,
    val isCompleted: Boolean = false
)

@Entity(tableName = "maintenance")
data class Maintenance(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val scheduledTime: Long,
    val isFilterClean: Boolean = false,
    val isGlassClean: Boolean = false,
    val isSubstrateClean: Boolean = false,
    val isFishOk: Boolean = false,
    val isCompleted: Boolean = false
)

// Data Access Object (DAO)
@Dao
interface MaskokiDao {
    // Feeding Queries
    @Query("SELECT * FROM feeding_schedule")
    fun getAllFeedingSchedules(): Flow<List<FeedingSchedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedingSchedule(schedule: FeedingSchedule): Long

    @Delete
    suspend fun deleteFeedingSchedule(schedule: FeedingSchedule)
    
    @Query("SELECT * FROM feeding_history ORDER BY timestamp DESC")
    fun getFeedingHistory(): Flow<List<FeedingHistory>>

    @Insert
    suspend fun insertFeedingHistory(history: FeedingHistory)

    @Delete
    suspend fun deleteFeedingHistory(history: FeedingHistory)

    // Water Change Queries
    @Query("SELECT * FROM water_change ORDER BY scheduledTime ASC")
    fun getAllWaterChanges(): Flow<List<WaterChange>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterChange(change: WaterChange)

    @Delete
    suspend fun deleteWaterChange(change: WaterChange)

    // Maintenance Queries
    @Query("SELECT * FROM maintenance ORDER BY scheduledTime ASC")
    fun getAllMaintenances(): Flow<List<Maintenance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenance(maintenance: Maintenance)

    @Delete
    suspend fun deleteMaintenance(maintenance: Maintenance)

    @Query("DELETE FROM feeding_schedule")
    suspend fun wipeFeedingSchedule()

    @Query("DELETE FROM feeding_history")
    suspend fun wipeFeedingHistory()

    @Query("DELETE FROM water_change")
    suspend fun wipeWaterChange()

    @Query("DELETE FROM maintenance")
    suspend fun wipeMaintenance()
}

// Database definition
@Database(
    entities = [
        FeedingSchedule::class, 
        FeedingHistory::class, 
        WaterChange::class, 
        Maintenance::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun maskokiDao(): MaskokiDao
}

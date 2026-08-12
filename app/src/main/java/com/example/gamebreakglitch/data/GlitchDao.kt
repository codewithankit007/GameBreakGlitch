package com.example.gamebreakglitch.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GlitchDao {
    @Query("SELECT * FROM glitch_settings WHERE id = 1")
    fun getSettings(): Flow<GlitchSettingsEntity?>

    @Query("SELECT * FROM glitch_settings WHERE id = 1")
    suspend fun getSettingsDirect(): GlitchSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: GlitchSettingsEntity)

    @Insert
    suspend fun logActivation(log: ActivationLogEntity)

    @Query("SELECT COUNT(*) FROM activation_logs")
    fun getTotalActivations(): Flow<Int>

    @Query("SELECT COUNT(*) FROM activation_logs WHERE timestamp >= :startOfDayTimestamp")
    fun getTodayActivations(startOfDayTimestamp: Long): Flow<Int>

    @Query("SELECT timestamp FROM activation_logs ORDER BY timestamp DESC LIMIT 1")
    fun getLastActivationTime(): Flow<Long?>
}

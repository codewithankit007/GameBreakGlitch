package com.example.gamebreakglitch.data

import kotlinx.coroutines.flow.Flow

class GlitchRepository(private val dao: GlitchDao) {
    val settings: Flow<GlitchSettingsEntity?> = dao.getSettings()
    val totalActivations: Flow<Int> = dao.getTotalActivations()

    suspend fun getSettingsDirect(): GlitchSettingsEntity {
        return dao.getSettingsDirect() ?: GlitchSettingsEntity()
    }

    suspend fun updateSettings(settings: GlitchSettingsEntity) {
        dao.saveSettings(settings)
    }

    suspend fun logActivation(mode: String, intensity: String, duration: Long) {
        dao.logActivation(ActivationLogEntity(triggerMode = mode, intensityLevel = intensity, durationSeconds = duration))
    }

    fun getTodayActivations(startOfDay: Long): Flow<Int> = dao.getTodayActivations(startOfDay)
    val lastActivationTime: Flow<Long?> = dao.getLastActivationTime()
}

package com.example.gamebreakglitch.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamebreakglitch.data.AppDatabase
import com.example.gamebreakglitch.data.GlitchRepository
import com.example.gamebreakglitch.data.GlitchSettingsEntity
import com.example.gamebreakglitch.service.GlitchOverlayService
import com.example.gamebreakglitch.util.SecurityUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GlitchRepository(AppDatabase.getDatabase(application).glitchDao())

    val settings: StateFlow<GlitchSettingsEntity> = repository.settings
        .map { it ?: GlitchSettingsEntity() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, GlitchSettingsEntity())

    val totalActivations: StateFlow<Int> = repository.totalActivations
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val todayActivations: StateFlow<Int> = repository.getTodayActivations(getStartOfDayTimestamp())
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val lastActivationTime: StateFlow<Long?> = repository.lastActivationTime
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    fun unlockWithPin(pin: String): Boolean {
        val currentHash = settings.value.pinHash
        val matches = SecurityUtils.verifyPin(pin, currentHash)
        if (matches) {
            _isUnlocked.value = true
        }
        return matches
    }

    fun updatePin(newPin: String) {
        viewModelScope.launch {
            val newHash = SecurityUtils.hashPin(newPin)
            repository.updateSettings(settings.value.copy(pinHash = newHash))
        }
    }

    fun updateSettings(newSettings: GlitchSettingsEntity) {
        viewModelScope.launch {
            repository.updateSettings(newSettings)
        }
    }

    fun startGlitchMode() {
        val context = getApplication<Application>()
        val intent = Intent(context, GlitchOverlayService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopGlitchMode() {
        val context = getApplication<Application>()
        val intent = Intent(context, GlitchOverlayService::class.java).apply {
            action = GlitchOverlayService.ACTION_STOP_GLITCH
        }
        context.startService(intent)
    }

    private fun getStartOfDayTimestamp(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}

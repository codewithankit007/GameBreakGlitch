package com.example.gamebreakglitch.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import com.example.gamebreakglitch.data.AppDatabase
import com.example.gamebreakglitch.data.GlitchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppTriggerAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var activePackageName: String? = null
    private val handler = Handler(Looper.getMainLooper())
    private var triggerRunnable: Runnable? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val pkgName = event.packageName?.toString() ?: return
            if (pkgName == activePackageName) return

            activePackageName = pkgName
            checkMonitoredApp(pkgName)
        }
    }

    private fun checkMonitoredApp(pkgName: String) {
        triggerRunnable?.let { handler.removeCallbacks(it) }

        scope.launch {
            val repository = GlitchRepository(AppDatabase.getDatabase(applicationContext).glitchDao())
            val settings = repository.getSettingsDirect()

            if (settings.triggerMode == "APP_TRIGGER") {
                val monitoredList = settings.monitoredPackages.split(",").map { it.trim() }
                if (monitoredList.contains(pkgName)) {
                    triggerRunnable = Runnable {
                        val serviceIntent = Intent(applicationContext, GlitchOverlayService::class.java)
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            startForegroundService(serviceIntent)
                        } else {
                            startService(serviceIntent)
                        }
                    }
                    handler.postDelayed(triggerRunnable!!, 60000L)
                }
            }
        }
    }

    override fun onInterrupt() {}
}

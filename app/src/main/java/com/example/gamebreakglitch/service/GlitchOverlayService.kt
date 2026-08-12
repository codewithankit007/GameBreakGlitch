package com.example.gamebreakglitch.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.gamebreakglitch.data.AppDatabase
import com.example.gamebreakglitch.data.GlitchRepository
import com.example.gamebreakglitch.ui.MainActivity
import com.example.gamebreakglitch.ui.components.FakeErrorDialogView
import com.example.gamebreakglitch.ui.components.GlitchCanvasView
import kotlinx.coroutines.*

class GlitchOverlayService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var windowManager: WindowManager
    private var overlayContainer: FrameLayout? = null
    private var glitchCanvasView: GlitchCanvasView? = null
    private var autoRecoveryHandler = Handler(Looper.getMainLooper())
    private var autoRecoveryRunnable: Runnable? = null
    private var touchStartTime = 0L

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        startForegroundServiceNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP_GLITCH) {
            stopGlitchOverlay()
            stopSelf()
            return START_NOT_STICKY
        }

        serviceScope.launch {
            val repository = GlitchRepository(AppDatabase.getDatabase(applicationContext).glitchDao())
            val settings = repository.getSettingsDirect()

            showGlitchOverlay(
                intensity = settings.intensityLevel,
                lcdLines = settings.lcdLinesEnabled,
                flicker = settings.screenFlickerEnabled,
                pixelCorrupt = settings.pixelCorruptionEnabled,
                colorDist = settings.colorDistortionEnabled,
                showDialog = settings.fakeErrorDialogsEnabled,
                recoveryTimeoutMins = settings.autoRecoveryDurationMinutes
            )

            repository.logActivation(
                mode = settings.triggerMode,
                intensity = settings.intensityLevel,
                duration = settings.autoRecoveryDurationMinutes * 60L
            )
        }

        return START_STICKY
    }

    private fun showGlitchOverlay(
        intensity: String,
        lcdLines: Boolean,
        flicker: Boolean,
        pixelCorrupt: Boolean,
        colorDist: Boolean,
        showDialog: Boolean,
        recoveryTimeoutMins: Int
    ) {
        if (overlayContainer != null) return

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        overlayContainer = FrameLayout(this)
        glitchCanvasView = GlitchCanvasView(this).apply {
            this.intensity = intensity
            this.lcdLinesEnabled = lcdLines
            this.screenFlickerEnabled = flicker
            this.pixelCorruptionEnabled = pixelCorrupt
            this.colorDistortionEnabled = colorDist
        }

        overlayContainer?.addView(glitchCanvasView)

        if (showDialog) {
            val fakeDialog = FakeErrorDialogView(this) {
                overlayContainer?.removeView(it)
            }
            val dialogParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
            overlayContainer?.addView(fakeDialog, dialogParams)
        }

        overlayContainer?.setOnTouchListener { _, event ->
            if (event.x < 300 && event.y < 300) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> touchStartTime = System.currentTimeMillis()
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - touchStartTime >= 5000) {
                            Toast.makeText(this, "Emergency Recovery Activated", Toast.LENGTH_SHORT).show()
                            stopGlitchOverlay()
                            stopSelf()
                        }
                    }
                }
            }
            false
        }

        windowManager.addView(overlayContainer, layoutParams)

        autoRecoveryRunnable = Runnable {
            stopGlitchOverlay()
            stopSelf()
        }
        autoRecoveryHandler.postDelayed(autoRecoveryRunnable!!, recoveryTimeoutMins * 60 * 1000L)
    }

    private fun stopGlitchOverlay() {
        autoRecoveryRunnable?.let { autoRecoveryHandler.removeCallbacks(it) }
        overlayContainer?.let {
            windowManager.removeView(it)
            overlayContainer = null
            glitchCanvasView = null
        }
    }

    private fun startForegroundServiceNotification() {
        val channelId = "glitch_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Game Break Simulation Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Game Break Safety Shield")
            .setContentText("Display simulation engine running.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        stopGlitchOverlay()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val NOTIFICATION_ID = 9901
        const val ACTION_STOP_GLITCH = "com.example.gamebreakglitch.STOP_GLITCH"
    }
}

package com.example.gamebreakglitch.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "glitch_settings")
data class GlitchSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val pinHash: String = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92",
    val isBiometricEnabled: Boolean = false,
    val intensityLevel: String = "MEDIUM",
    val lcdLinesEnabled: Boolean = true,
    val screenFlickerEnabled: Boolean = true,
    val pixelCorruptionEnabled: Boolean = true,
    val colorDistortionEnabled: Boolean = true,
    val fakeErrorDialogsEnabled: Boolean = true,
    val autoRecoveryDurationMinutes: Int = 5,
    val triggerMode: String = "MANUAL",
    val monitoredPackages: String = "com.dts.freefireth,com.pubg.imobile,com.roblox.client,com.mojang.minecraftpe"
)

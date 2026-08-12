package com.example.gamebreakglitch.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activation_logs")
data class ActivationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val triggerMode: String,
    val intensityLevel: String,
    val durationSeconds: Long
)

package com.example.smartenergy.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "homes")
data class HomeEntity(
    @PrimaryKey val homeId: String,
    val name: String,
    val mqttHost: String,
    val mqttPort: Int,
    val mqttUsername: String,
    val mqttPassword: String
)

@Entity(
    tableName = "rooms",
    foreignKeys = [ForeignKey(
        entity = HomeEntity::class,
        parentColumns = ["homeId"],
        childColumns = ["homeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("homeId")]
)
data class RoomEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val homeId: String,
    val name: String
)

@Entity(
    tableName = "devices",
    indices = [Index(value = ["deviceId"], unique = true), Index("roomId")],
    foreignKeys = [ForeignKey(
        entity = RoomEntity::class,
        parentColumns = ["id"],
        childColumns = ["roomId"],
        onDelete = ForeignKey.SET_NULL
    )]
)
data class DeviceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val homeId: String,
    val deviceId: String,
    val roomId: Long?,
    val roomName: String,
    val name: String,
    val stateTopic: String,
    val telemetryTopic: String,
    val commandTopic: String,
    val lwtTopic: String,
    val isOn: Boolean = false,
    val isOnline: Boolean = false,
    val power: Double = 0.0,
    val energy: Double = 0.0,
    val lastUpdatedTs: Long = 0
)

@Entity(tableName = "telemetry", indices = [Index("deviceId"), Index("ts")])
data class TelemetryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceId: String,
    val voltage: Double,
    val current: Double,
    val power: Double,
    val energy: Double,
    val freq: Double,
    val pf: Double,
    val ts: Long
)

@Entity(tableName = "rules", indices = [Index("deviceId")])
data class RuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceId: String?,
    val type: String,
    val enabled: Boolean,
    val threshold: Double,
    val durationMin: Int,
    val startTime: String,
    val endTime: String
)

@Entity(tableName = "schedules", indices = [Index("deviceId")])
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceId: String,
    val atTime: String,
    val turnOn: Boolean,
    val enabled: Boolean = true
)

package com.example.smartenergy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        HomeEntity::class,
        RoomEntity::class,
        DeviceEntity::class,
        TelemetryEntity::class,
        RuleEntity::class,
        ScheduleEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SmartEnergyDatabase : RoomDatabase() {
    abstract fun dao(): SmartEnergyDao
}

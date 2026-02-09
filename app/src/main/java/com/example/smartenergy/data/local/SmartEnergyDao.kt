package com.example.smartenergy.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SmartEnergyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHome(home: HomeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRoom(room: RoomEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDevice(device: DeviceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTelemetry(t: TelemetryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRule(rule: RuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSchedule(schedule: ScheduleEntity)

    @Query("SELECT * FROM devices ORDER BY roomName, name")
    fun observeDevices(): Flow<List<DeviceEntity>>

    @Query("SELECT * FROM devices WHERE deviceId = :deviceId LIMIT 1")
    fun observeDevice(deviceId: String): Flow<DeviceEntity?>

    @Query("SELECT * FROM telemetry WHERE deviceId = :deviceId ORDER BY ts DESC LIMIT :limit")
    fun observeTelemetry(deviceId: String, limit: Int = 50): Flow<List<TelemetryEntity>>

    @Query("SELECT * FROM schedules WHERE deviceId = :deviceId ORDER BY atTime")
    fun observeSchedules(deviceId: String): Flow<List<ScheduleEntity>>

    @Query("UPDATE devices SET isOn = :on, lastUpdatedTs = :ts WHERE deviceId = :deviceId")
    suspend fun updateDeviceState(deviceId: String, on: Boolean, ts: Long)

    @Query("UPDATE devices SET isOnline = :online, lastUpdatedTs = :ts WHERE deviceId = :deviceId")
    suspend fun updateDeviceLwt(deviceId: String, online: Boolean, ts: Long)

    @Query("UPDATE devices SET power = :power, energy = :energy, lastUpdatedTs = :ts WHERE deviceId = :deviceId")
    suspend fun updateTelemetrySnapshot(deviceId: String, power: Double, energy: Double, ts: Long)

    @Query("SELECT SUM(power) FROM telemetry WHERE ts BETWEEN :fromTs AND :toTs")
    suspend fun sumPower(fromTs: Long, toTs: Long): Double?

    @Query("SELECT MAX(energy) - MIN(energy) FROM telemetry WHERE ts BETWEEN :fromTs AND :toTs")
    suspend fun consumedEnergy(fromTs: Long, toTs: Long): Double?

    @Query("SELECT * FROM rules WHERE enabled = 1")
    suspend fun getEnabledRules(): List<RuleEntity>

    @Query("SELECT * FROM devices")
    suspend fun getDevicesOnce(): List<DeviceEntity>
}

package com.example.smartenergy.domain.repository

import com.example.smartenergy.data.local.ScheduleEntity
import com.example.smartenergy.domain.model.Device
import com.example.smartenergy.domain.model.EnergyStats
import com.example.smartenergy.domain.model.Telemetry
import kotlinx.coroutines.flow.Flow

interface SmartEnergyRepository {
    fun observeDevices(): Flow<List<Device>>
    fun observeDevice(deviceId: String): Flow<Device?>
    fun observeTelemetry(deviceId: String): Flow<List<Telemetry>>
    fun observeSchedules(deviceId: String): Flow<List<ScheduleEntity>>
    suspend fun toggleDevice(deviceId: String, on: Boolean)
    suspend fun saveTelemetry(telemetry: Telemetry)
    suspend fun getStats(fromTs: Long, toTs: Long): EnergyStats
    suspend fun createSchedule(schedule: ScheduleEntity)
    suspend fun evaluateRules()
}

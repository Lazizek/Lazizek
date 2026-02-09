package com.example.smartenergy.domain.usecase

import com.example.smartenergy.data.local.ScheduleEntity
import com.example.smartenergy.domain.model.Device
import com.example.smartenergy.domain.model.EnergyStats
import com.example.smartenergy.domain.model.Telemetry
import com.example.smartenergy.domain.repository.SmartEnergyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ToggleDeviceUseCase @Inject constructor(private val repo: SmartEnergyRepository) {
    suspend operator fun invoke(deviceId: String, on: Boolean) = repo.toggleDevice(deviceId, on)
}

class ObserveDeviceStateUseCase @Inject constructor(private val repo: SmartEnergyRepository) {
    operator fun invoke(): Flow<List<Device>> = repo.observeDevices()
}

class SaveTelemetryUseCase @Inject constructor(private val repo: SmartEnergyRepository) {
    suspend operator fun invoke(telemetry: Telemetry) = repo.saveTelemetry(telemetry)
}

class GetStatsUseCase @Inject constructor(private val repo: SmartEnergyRepository) {
    suspend operator fun invoke(fromTs: Long, toTs: Long): EnergyStats = repo.getStats(fromTs, toTs)
}

class CreateScheduleUseCase @Inject constructor(private val repo: SmartEnergyRepository) {
    suspend operator fun invoke(schedule: ScheduleEntity) = repo.createSchedule(schedule)
}

class EvaluateRulesUseCase @Inject constructor(private val repo: SmartEnergyRepository) {
    suspend operator fun invoke() = repo.evaluateRules()
}

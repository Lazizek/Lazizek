package com.example.smartenergy.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartenergy.data.local.ScheduleEntity
import com.example.smartenergy.domain.model.Telemetry
import com.example.smartenergy.domain.repository.SmartEnergyRepository
import com.example.smartenergy.domain.usecase.CreateScheduleUseCase
import com.example.smartenergy.domain.usecase.ToggleDeviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceDetailViewModel @Inject constructor(
    private val repo: SmartEnergyRepository,
    private val toggleDeviceUseCase: ToggleDeviceUseCase,
    private val createScheduleUseCase: CreateScheduleUseCase
) : ViewModel() {
    fun observeDevice(deviceId: String) = repo.observeDevice(deviceId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun observeTelemetry(deviceId: String) = repo.observeTelemetry(deviceId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<Telemetry>())

    fun observeSchedules(deviceId: String) = repo.observeSchedules(deviceId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<ScheduleEntity>())

    fun toggle(deviceId: String, on: Boolean) = viewModelScope.launch { toggleDeviceUseCase(deviceId, on) }

    fun createSchedule(deviceId: String, at: String, turnOn: Boolean) = viewModelScope.launch {
        createScheduleUseCase(ScheduleEntity(deviceId = deviceId, atTime = at, turnOn = turnOn))
    }
}

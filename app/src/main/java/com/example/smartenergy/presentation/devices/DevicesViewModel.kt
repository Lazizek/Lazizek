package com.example.smartenergy.presentation.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartenergy.domain.model.Device
import com.example.smartenergy.domain.usecase.ObserveDeviceStateUseCase
import com.example.smartenergy.domain.usecase.ToggleDeviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    observeDeviceStateUseCase: ObserveDeviceStateUseCase,
    private val toggleDeviceUseCase: ToggleDeviceUseCase
) : ViewModel() {
    val devices: StateFlow<List<Device>> = observeDeviceStateUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggle(device: Device, on: Boolean) = viewModelScope.launch {
        toggleDeviceUseCase(device.deviceId, on)
    }
}

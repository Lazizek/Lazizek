package com.example.smartenergy.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartenergy.domain.usecase.GetStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getStatsUseCase: GetStatsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow("0 kWh")
    val state: StateFlow<String> = _state

    fun loadWeekly() = viewModelScope.launch {
        val now = System.currentTimeMillis() / 1000
        val weekAgo = now - 7 * 24 * 3600
        val stats = getStatsUseCase(weekAgo, now)
        _state.value = "Hafta: ${"%.2f".format(stats.totalEnergy)} kWh"
    }
}

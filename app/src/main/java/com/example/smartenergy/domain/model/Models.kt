package com.example.smartenergy.domain.model

data class Device(
    val deviceId: String,
    val name: String,
    val room: String,
    val isOn: Boolean,
    val isOnline: Boolean,
    val power: Double,
    val energy: Double
)

data class Telemetry(
    val deviceId: String,
    val power: Double,
    val energy: Double,
    val ts: Long
)

data class EnergyStats(
    val totalPower: Double,
    val totalEnergy: Double
)

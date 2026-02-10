package com.example.smartenergy.data.repository

import com.example.smartenergy.data.local.DeviceEntity

import com.example.smartenergy.data.local.HomeEntity
import com.example.smartenergy.data.local.RoomEntity
import com.example.smartenergy.data.local.ScheduleEntity
import com.example.smartenergy.data.local.SmartEnergyDao
import com.example.smartenergy.data.local.TelemetryEntity
import com.example.smartenergy.data.mqtt.MqttClientWrapper
import com.example.smartenergy.domain.model.Device
import com.example.smartenergy.domain.model.EnergyStats
import com.example.smartenergy.domain.model.Telemetry
import com.example.smartenergy.domain.repository.SmartEnergyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartEnergyRepositoryImpl @Inject constructor(
    private val dao: SmartEnergyDao,
    private val mqtt: MqttClientWrapper
) : SmartEnergyRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            dao.upsertHome(HomeEntity("demo", "My Home", "192.168.1.100", 1883, "user", "pass"))
            dao.upsertRoom(RoomEntity(homeId = "demo", name = "Living room"))
            dao.upsertDevice(
                DeviceEntity(
                    homeId = "demo",
                    deviceId = "plug-1",
                    roomId = null,
                    roomName = "Living room",
                    name = "TV Plug",
                    stateTopic = "home/demo/device/plug-1/state",
                    telemetryTopic = "home/demo/device/plug-1/telemetry",
                    commandTopic = "home/demo/device/plug-1/command",
                    lwtTopic = "home/demo/device/plug-1/lwt"
                )
            )
            mqtt.incomingMessages().collect { (topic, payload) -> handleIncoming(topic, payload) }
        }
    }

    override fun observeDevices(): Flow<List<Device>> = dao.observeDevices().map { list -> list.map { it.toDomain() } }

    override fun observeDevice(deviceId: String): Flow<Device?> = dao.observeDevice(deviceId).map { it?.toDomain() }

    override fun observeTelemetry(deviceId: String): Flow<List<Telemetry>> =
        dao.observeTelemetry(deviceId).map { items -> items.map { Telemetry(it.deviceId, it.power, it.energy, it.ts) } }

    override fun observeSchedules(deviceId: String): Flow<List<ScheduleEntity>> = dao.observeSchedules(deviceId)

    override suspend fun toggleDevice(deviceId: String, on: Boolean) {
        val commandTopic = "home/demo/device/$deviceId/command"
        mqtt.publishCommand(commandTopic, "{\"cmd\":\"set\",\"on\":$on,\"ts\":${System.currentTimeMillis() / 1000}}")
        dao.updateDeviceState(deviceId, on, System.currentTimeMillis())
    }

    override suspend fun saveTelemetry(telemetry: Telemetry) {
        dao.insertTelemetry(TelemetryEntity(deviceId = telemetry.deviceId, voltage = 220.0, current = 0.0, power = telemetry.power, energy = telemetry.energy, freq = 50.0, pf = 0.9, ts = telemetry.ts))
        dao.updateTelemetrySnapshot(telemetry.deviceId, telemetry.power, telemetry.energy, telemetry.ts)
    }

    override suspend fun getStats(fromTs: Long, toTs: Long): EnergyStats {
        return EnergyStats(dao.sumPower(fromTs, toTs) ?: 0.0, dao.consumedEnergy(fromTs, toTs) ?: 0.0)
    }

    override suspend fun createSchedule(schedule: ScheduleEntity) {
        dao.upsertSchedule(schedule)
    }

    override suspend fun evaluateRules() {
        val rules = dao.getEnabledRules()
        val devices = dao.getDevicesOnce().associateBy { it.deviceId }
        rules.forEach { rule ->
            if (rule.type == "POWER_LIMIT" && rule.deviceId != null) {
                val d = devices[rule.deviceId] ?: return@forEach
                if (d.power > rule.threshold) toggleDevice(d.deviceId, false)
            }
        }
    }

    private suspend fun handleIncoming(topic: String, payload: String) {
        val deviceId = topic.split("/").getOrNull(3) ?: return
        val json = JSONObject(payload)
        when {
            topic.endsWith("/state") -> dao.updateDeviceState(deviceId, json.optBoolean("on"), json.optLong("ts"))
            topic.endsWith("/lwt") -> dao.updateDeviceLwt(deviceId, json.optBoolean("online"), json.optLong("ts"))
            topic.endsWith("/telemetry") -> {
                val t = TelemetryEntity(
                    deviceId = deviceId,
                    voltage = json.optDouble("voltage"),
                    current = json.optDouble("current"),
                    power = json.optDouble("power"),
                    energy = json.optDouble("energy"),
                    freq = json.optDouble("freq"),
                    pf = json.optDouble("pf"),
                    ts = json.optLong("ts")
                )
                dao.insertTelemetry(t)
                dao.updateTelemetrySnapshot(deviceId, t.power, t.energy, t.ts)
            }
        }
    }
}

private fun DeviceEntity.toDomain() = Device(deviceId, name, roomName, isOn, isOnline, power, energy)

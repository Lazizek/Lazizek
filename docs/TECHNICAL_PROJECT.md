# Smart Energy Home — Texnik loyiha (Android + ESP32 + MQTT)

## A) PRD + MVP Scope
- **Muammo:** Uy qurilmalarining nazoratsiz energiya sarfi.
- **Yechim:** Lokal-first Android ilova ESP32 relay + sensorlardan real-time telemetry olib, boshqaruv/schedule/rules/statistika beradi.
- **MVP:**
  1. Device add (manual + QR format parser)
  2. Device list (ON/OFF, W, kWh, online)
  3. Toggle command (MQTT)
  4. Schedule (time-based ON/OFF)
  5. Rules: power limit, max ON duration, night mode
  6. Daily/weekly stats (Room query + chart)
  7. Offline cache (Room)

## B) Arxitektura (matn diagramma)
```
[ESP32 + Relay + PZEM/HLW] <--MQTT JSON--> [Mosquitto (LAN)] <--TCP--> [Android App]

Android:
presentation (Activity/Fragments/ViewModels)
  -> domain (UseCases, Models, Repository Interface)
    -> data (Repository Impl, MQTT Wrapper, Room DAO/DB)
```

## MQTT standartlari (qattiq)
- state: `home/{homeId}/device/{deviceId}/state`
- telemetry: `home/{homeId}/device/{deviceId}/telemetry`
- command: `home/{homeId}/device/{deviceId}/command`
- lwt: `home/{homeId}/device/{deviceId}/lwt`

Payloadlar:
```json
{"cmd":"set","on":true,"ts":1700000000}
{"on":true,"relay":1,"ts":1700000001}
{"voltage":220.4,"current":0.31,"power":68.2,"energy":1.42,"freq":50.0,"pf":0.91,"ts":1700000002}
{"online":true,"ts":1700000002}
```

## C) Data model (Room)
- `HomeEntity(homeId, mqtt config)`
- `RoomEntity(homeId->FK)`
- `DeviceEntity(deviceId unique, topics, snapshot)`
- `TelemetryEntity(deviceId, W/V/A/kWh, ts)`
- `RuleEntity(type, threshold, duration, time window)`
- `ScheduleEntity(deviceId, atTime, turnOn)`

Indekslar: deviceId unique, ts index, FK indekslar.

## D) MQTT wrapper
- connect/disconnect
- subscribe multiple topics
- publish command
- incoming message map -> state/telemetry/lwt
- Tavsiya: continuous connection uchun **Foreground service**, periodik checks uchun WorkManager.

## E) Domain use-cases
- `ToggleDeviceUseCase`
- `ObserveDeviceStateUseCase`
- `SaveTelemetryUseCase`
- `GetStatsUseCase`
- `CreateScheduleUseCase`
- `EvaluateRulesUseCase`

## F) Presentation
- Devices List: RecyclerView + DiffUtil + real-time snapshot
- Device Detail: history/schedule + command publish
- Automations: rule create (UI skeleton)
- Stats: Room query + Line chart

## G) DI (Hilt)
- `SmartEnergyApp` (`@HiltAndroidApp`)
- `AppModule` DB/DAO/Repository providers

## H) WorkManager
- `ScheduleExecutorWorker` (exact/alarm qo‘shimcha bilan)
- `RuleCheckerWorker` (15 min interval)
- Cleanup worker (telemetry retention, masalan 30 kun)

## Presence (ixtiyoriy)
- Wi-Fi SSID check orqali `home/away` holat.

## Cloud sync (ixtiyoriy)
- Firebase Auth + Firestore: settings/rules backup, multi-device sync.
- Lokal-first saqlanadi, network bo‘lsa background sync.

## Security
- MQTT username/password majburiy.
- TLS (self-signed CA pinning) tavsiya.
- Qurilma `deviceId` whitelist.

## Energy saving tips bo‘limi
- Standby qurilmalarni kechasi OFF
- Har xona uchun power limit
- Peak time’da navbat bilan ishlatish

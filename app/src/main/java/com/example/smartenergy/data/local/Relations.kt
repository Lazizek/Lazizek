package com.example.smartenergy.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class RoomWithDevices(
    @Embedded val room: RoomEntity,
    @Relation(parentColumn = "id", entityColumn = "roomId")
    val devices: List<DeviceEntity>
)

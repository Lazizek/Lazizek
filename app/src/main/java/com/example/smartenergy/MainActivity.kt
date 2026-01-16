package com.example.smartenergy

import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvRecommendation: TextView
    private lateinit var tvEstimatedUsage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val swLights = findViewById<Switch>(R.id.swLights)
        val swHeating = findViewById<Switch>(R.id.swHeating)
        val swFridge = findViewById<Switch>(R.id.swFridge)
        val swWasher = findViewById<Switch>(R.id.swWasher)
        val swAC = findViewById<Switch>(R.id.swAC)
        val btnOptimize = findViewById<Button>(R.id.btnOptimize)

        tvRecommendation = findViewById(R.id.tvRecommendation)
        tvEstimatedUsage = findViewById(R.id.tvEstimatedUsage)

        btnOptimize.setOnClickListener {
            val devices = listOf(
                Device("Yoritish", swLights.isChecked, 0.7),
                Device("Isitish", swHeating.isChecked, 2.5),
                Device("Muzlatgich", swFridge.isChecked, 1.2),
                Device("Kir yuvish", swWasher.isChecked, 1.8),
                Device("Konditsioner", swAC.isChecked, 2.0)
            )

            val plan = EnergyOptimizer.optimize(devices)
            tvEstimatedUsage.text = getString(
                R.string.estimated_usage_format,
                plan.estimatedKwh
            )
            tvRecommendation.text = plan.recommendation
        }
    }
}

data class Device(
    val name: String,
    val enabled: Boolean,
    val powerKwh: Double
)

data class OptimizationPlan(
    val estimatedKwh: Double,
    val recommendation: String
)

object EnergyOptimizer {

    fun optimize(devices: List<Device>): OptimizationPlan {
        val activeDevices = devices.filter { it.enabled }
        val baseUsage = activeDevices.sumOf { it.powerKwh }
        val heavyDevices = activeDevices.filter { it.powerKwh >= 2.0 }

        val recommendation = buildString {
            if (activeDevices.isEmpty()) {
                append("Hozircha faol qurilmalar yo'q. Tejash rejimi allaqachon yoqilgan.")
                return@buildString
            }

            append("Tavsiya: ")
            if (heavyDevices.isNotEmpty()) {
                append("eng ko'p energiya sarflaydigan qurilmalarni navbat bilan ishlating: ")
                append(heavyDevices.joinToString { it.name })
                append(". ")
            } else {
                append("foydalanilayotgan qurilmalar samarali ishlamoqda. ")
            }

            val lightDevices = activeDevices.filter { it.powerKwh < 1.0 }
            if (lightDevices.isNotEmpty()) {
                append("Yoritishni har 30 daqiqada tekshirib, kerak bo'lmasa o'chiring.")
            } else {
                append("Kun davomida rejali ishlash grafikini saqlang.")
            }
        }

        val optimizedUsage = baseUsage * 0.88
        return OptimizationPlan(optimizedUsage, recommendation)
    }
}

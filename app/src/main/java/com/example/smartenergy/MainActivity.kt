package com.example.smartenergy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.smartenergy.databinding.ActivityMainBinding
import com.example.smartenergy.presentation.automation.AutomationsFragment
import com.example.smartenergy.presentation.devices.DevicesFragment
import com.example.smartenergy.presentation.stats.StatsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, DevicesFragment())
            }
        }

        binding.bottomNav.setOnItemSelectedListener {
            val fragment = when (it.itemId) {
                R.id.nav_devices -> DevicesFragment()
                R.id.nav_automations -> AutomationsFragment()
                else -> StatsFragment()
            }
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, fragment)
            }
            true
        }
    }
}

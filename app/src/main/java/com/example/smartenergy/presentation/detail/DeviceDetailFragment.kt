package com.example.smartenergy.presentation.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.smartenergy.R
import com.example.smartenergy.databinding.FragmentDeviceDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeviceDetailFragment : Fragment(R.layout.fragment_device_detail) {
    private var _binding: FragmentDeviceDetailBinding? = null
    private val binding get() = _binding!!
    private val vm: DeviceDetailViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentDeviceDetailBinding.bind(view)
        val deviceId = requireArguments().getString(ARG_DEVICE_ID).orEmpty()

        binding.btnSaveSchedule.setOnClickListener {
            vm.createSchedule(deviceId, binding.etTime.text.toString(), binding.switchScheduleOn.isChecked)
        }
        binding.switchPower.setOnCheckedChangeListener { _, on -> vm.toggle(deviceId, on) }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.observeDevice(deviceId).collect { device ->
                binding.tvDeviceTitle.text = device?.name ?: deviceId
                binding.switchPower.isChecked = device?.isOn ?: false
                binding.tvRealtime.text = "${device?.power ?: 0.0} W • ${device?.energy ?: 0.0} kWh"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.observeSchedules(deviceId).collect { schedules ->
                binding.tvSchedules.text = schedules.joinToString("\n") { "${it.atTime} -> ${if (it.turnOn) "ON" else "OFF"}" }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_DEVICE_ID = "device_id"
        fun newInstance(deviceId: String): DeviceDetailFragment = DeviceDetailFragment().apply {
            arguments = Bundle().apply { putString(ARG_DEVICE_ID, deviceId) }
        }
    }
}

package com.example.smartenergy.presentation.devices

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartenergy.R
import com.example.smartenergy.databinding.FragmentDevicesBinding
import com.example.smartenergy.presentation.detail.DeviceDetailFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DevicesFragment : Fragment(R.layout.fragment_devices) {
    private var _binding: FragmentDevicesBinding? = null
    private val binding get() = _binding!!
    private val vm: DevicesViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDevicesBinding.bind(view)

        val adapter = DevicesAdapter(
            onToggle = vm::toggle,
            onClick = { device ->
                parentFragmentManager.commit {
                    replace(R.id.fragmentContainer, DeviceDetailFragment.newInstance(device.deviceId))
                    addToBackStack(null)
                }
            }
        )
        binding.recyclerDevices.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerDevices.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            vm.devices.collect(adapter::submitList)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

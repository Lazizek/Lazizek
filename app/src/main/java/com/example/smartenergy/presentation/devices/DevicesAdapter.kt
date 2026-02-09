package com.example.smartenergy.presentation.devices

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartenergy.databinding.ItemDeviceBinding
import com.example.smartenergy.domain.model.Device

class DevicesAdapter(
    private val onToggle: (Device, Boolean) -> Unit,
    private val onClick: (Device) -> Unit
) : ListAdapter<Device, DevicesAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<Device>() {
        override fun areItemsTheSame(oldItem: Device, newItem: Device) = oldItem.deviceId == newItem.deviceId
        override fun areContentsTheSame(oldItem: Device, newItem: Device) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val binding: ItemDeviceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Device) {
            binding.tvName.text = item.name
            binding.tvMeta.text = "${item.room} • ${item.power}W • ${item.energy}kWh"
            binding.switchOn.isChecked = item.isOn
            binding.tvOnline.text = if (item.isOnline) "Online" else "Offline"
            binding.switchOn.setOnCheckedChangeListener { _, on -> onToggle(item, on) }
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}

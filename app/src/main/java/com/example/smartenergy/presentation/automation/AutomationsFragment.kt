package com.example.smartenergy.presentation.automation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.smartenergy.R
import com.example.smartenergy.databinding.FragmentAutomationsBinding

class AutomationsFragment : Fragment(R.layout.fragment_automations) {
    private var _binding: FragmentAutomationsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentAutomationsBinding.bind(view)
        binding.tvRules.text = "Rules:\n1) Power > limit => notify + OFF\n2) ON duration > X min => notify\n3) Night mode => all OFF"
        binding.tvTips.text = "Energy saving tips:\n• Standby qurilmalarni tunda uzing\n• Limitlarni xonalar bo'yicha belgilang\n• Peak vaqtda og'ir yuklamani kamaytiring"
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

package com.oscarcreator.sms_scheduler_v2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentStatisticsBinding


/**
 * A simple [Fragment] subclass.
 * Use the [StatisticsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.tvTotalTreatments.text = "523"
        binding.tvProcentTreatments.text = "+14% this month"
        binding.tvTotalEarnings.text = "313800kr"
        binding.tvTotalEarningsExclusive.text = "164745kr"

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}
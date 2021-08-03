package com.oscarcreator.sms_scheduler_v2.treatments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentTreatmentsBinding

class TreatmentsFragment : Fragment() {

    private var _binding: FragmentTreatmentsBinding? = null

    private val binding: FragmentTreatmentsBinding
        get() = _binding!!

    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTreatmentsBinding.inflate(inflater, container, false)
        database = AppDatabase.getDatabase(requireContext(), lifecycleScope)

        val adapter = TreatmentsAdapter(TreatmentsAdapter.OnTreatmentClickedListener{
            val action = TreatmentsFragmentDirections.actionTreatmentsFragmentToTreatmentDetailFragment(it.id)
            findNavController().navigate(action)
        })

        database.treatmentDao().getTreatments().observe(viewLifecycleOwner) {
            adapter.setTreatments(it)
            adapter.notifyDataSetChanged()
        }

        binding.rvTreatments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }

        binding.fabAddTreatment.setOnClickListener {
            val action = TreatmentsFragmentDirections.actionTreatmentsFragmentToAddEditTreatment()
            findNavController().navigate(action)
        }

        return binding.root
    }
}
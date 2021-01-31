package com.oscarcreator.sms_scheduler_v2.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.data.scheduled.DefaultScheduledTreatmentRepository
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentUpcomingTreatmentCardListBinding

class UpcomingTreatmentCardListFragment : Fragment() {

    private var _binding: FragmentUpcomingTreatmentCardListBinding? = null
    private val binding: FragmentUpcomingTreatmentCardListBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingTreatmentCardListBinding.inflate(inflater, container, false)

        val layoutManager = LinearLayoutManager(requireContext())
        val adapter = UpcomingTreatmentAdapter()

        binding.upcomingTreatmentRecyclerView.apply {
            this.layoutManager = layoutManager
            this.adapter = adapter

        }
        val database = AppDatabase.getDatabase(requireContext(), lifecycleScope)
        val upcomingTreatmentCardListViewModel = UpcomingTreatmentCardListViewModel(
                DefaultScheduledTreatmentRepository.getInstance(
                    database.scheduledTreatmentDao(),
                    database.scheduledTreatmentCrossRefDao()
                )
            )

        upcomingTreatmentCardListViewModel.upcomingTreatments.observe(viewLifecycleOwner, {
            adapter.setScheduledTreatments(it)
        })


        return binding.root
    }


}
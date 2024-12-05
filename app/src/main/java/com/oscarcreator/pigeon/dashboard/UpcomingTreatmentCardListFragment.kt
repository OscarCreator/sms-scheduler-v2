package com.oscarcreator.pigeon.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.composethemeadapter.MdcTheme
import com.oscarcreator.pigeon.data.AppDatabase
import com.oscarcreator.pigeon.data.scheduled.DefaultScheduledTreatmentsRepository
import com.oscarcreator.pigeon.data.scheduled.local.ScheduledTreatmentsLocalDataSource
import com.oscarcreator.pigeon.databinding.FragmentUpcomingTreatmentCardListBinding
import com.oscarcreator.pigeon.scheduledtreatments.ScheduledTreatmentCard

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

        val database = AppDatabase.getDatabase(requireContext(), lifecycleScope)
        val upcomingTreatmentCardListViewModel = UpcomingTreatmentCardListViewModel(
            DefaultScheduledTreatmentsRepository(
                ScheduledTreatmentsLocalDataSource(
                    database.scheduledTreatmentDao()
                )
            )
        )

        binding.cvAppointments.setContent {
            MdcTheme {
                val scheduledTreatments by upcomingTreatmentCardListViewModel.upcomingTreatments.observeAsState()

                LazyColumn {
                    scheduledTreatments?.let {
                        items(it.take(3)) { scheduledTreatment ->
                            ScheduledTreatmentCard(scheduledTreatment) {
                                val action = DashboardFragmentDirections
                                    .actionDashboardFragmentToScheduledTreatmentDetailFragment(
                                        scheduledTreatment.scheduledTreatment.scheduledTreatmentId
                                    )
                                findNavController().navigate(action)
                            }
                        }
                    }
                }
            }
        }

        binding.btnViewAll.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToScheduledTreatmentsFragment())
        }

        return binding.root
    }
}
package com.oscarcreator.sms_scheduler_v2.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.TreatmentsStatsRepository
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        setUpStatisticsView()

        return binding.root
    }

    private fun setUpStatisticsView(){
        val dashboardViewModel = DashboardViewModel(TreatmentsStatsRepository.getInstance())

        dashboardViewModel.apply {
            setTotalTreatments(140)
            setProcentTreatments(9)
            setTotalEarnings(4250)
            setTotalEarningsExclusive(2530)

            totalTreatments.observe(viewLifecycleOwner, {
                binding.statisticsView.setTotalTreatments(it)
            })

            procentTreatments.observe(viewLifecycleOwner, {
                binding.statisticsView.setProcentTreatments(it)
            })

            totalEarnings.observe(viewLifecycleOwner, {
                binding.statisticsView.setTotalEarnings(it)
            })

            totalEarningsExclusive.observe(viewLifecycleOwner, {
                binding.statisticsView.setTotalEarningsExclusive(it)
            })

        }

        val upcomingTreatmentCardListFragment = UpcomingTreatmentCardListFragment()

        childFragmentManager.beginTransaction()
            .add(R.id.upcoming_treatment_list_fragment_container, upcomingTreatmentCardListFragment)
            .commit()
    }

}
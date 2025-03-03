package com.oscarcreator.pigeon.dashboard

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.oscarcreator.pigeon.R
import com.oscarcreator.pigeon.data.TreatmentsStatsRepository
import com.oscarcreator.pigeon.databinding.FragmentDashboardBinding
import com.oscarcreator.pigeon.notifications.NotificationsFragment

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){

            R.id.settings -> {
                val action = DashboardFragmentDirections.actionDashboardFragmentToSettingsFragment()
                findNavController().navigate(action)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        childFragmentManager.beginTransaction()
            .replace(R.id.fc_notifications, NotificationsFragment())
            .commit()

        binding.fabAddTreatment.setOnClickListener{
            val action = DashboardFragmentDirections.actionDashboardFragmentToAddEditTreatmentFragment(title = getString(R.string.add))
            findNavController().navigate(action)
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.upcoming_treatment_list_fragment_container, UpcomingTreatmentCardListFragment())
            .commit()

        return binding.root
    }

    private fun setUpStatisticsView(){
        val dashboardViewModel = DashboardViewModel(TreatmentsStatsRepository.getInstance())

        dashboardViewModel.apply {
            setTotalTreatments(140)
            setProcentTreatments(9)
            setTotalEarnings(4250)
            setTotalEarningsExclusive(2530)

            totalTreatments.observe(viewLifecycleOwner) {
                binding.statisticsView.setTotalTreatments(it)
            }

            procentTreatments.observe(viewLifecycleOwner) {
                binding.statisticsView.setProcentTreatments(it)
            }

            totalEarnings.observe(viewLifecycleOwner) {
                binding.statisticsView.setTotalEarnings(it)
            }

            totalEarningsExclusive.observe(viewLifecycleOwner) {
                binding.statisticsView.setTotalEarningsExclusive(it)
            }
        }
    }

}
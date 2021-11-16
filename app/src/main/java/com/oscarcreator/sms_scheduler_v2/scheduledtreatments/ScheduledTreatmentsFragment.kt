package com.oscarcreator.sms_scheduler_v2.scheduledtreatments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentScheduledtreatmentsBinding

class ScheduledTreatmentsFragment : Fragment() {

    private var _binding: FragmentScheduledtreatmentsBinding? = null
    private val binding: FragmentScheduledtreatmentsBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduledtreatmentsBinding.inflate(inflater, container, false)

        val view = binding.root

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        viewPager.offscreenPageLimit = 1

        val adapter = ScheduledTreatmentsViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapter

        val tabNames = arrayOf(getString(R.string.upcoming), getString(R.string.archived))
        TabLayoutMediator(tabLayout, viewPager) {tab, position ->
            tab.text = tabNames[position]
        }.attach()

        return view
    }


    inner class ScheduledTreatmentsViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        private val NUM_TABS = 2

        override fun getItemCount(): Int = NUM_TABS

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> UpcomingScheduledTreatmentsFragment()
                else -> ArchivedScheduledTreatmentsFragment()
            }
        }

    }

}
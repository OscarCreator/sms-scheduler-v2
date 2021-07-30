package com.oscarcreator.sms_scheduler_v2.scheduledtreatments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentScheduledtreatmentsUpcomingBinding
import java.util.*

class UpcomingScheduledTreatmentsFragment : Fragment() {

    private var _binding: FragmentScheduledtreatmentsUpcomingBinding? = null
    private val binding
        get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduledtreatmentsUpcomingBinding.inflate(inflater, container, false)

        val adapter = ScheduledTreatmentAdapter()

        adapter.setOnScheduledTreatmentClickedListener(
            ScheduledTreatmentAdapter.OnScheduledTreatmentClickedListener { position, scheduledTreatment ->
                //TODO open detail fragment
                //TODO only able to edit those which is not delivered

                val action = ScheduledTreatmentsFragmentDirections
                    .actionScheduledTreatmentsFragmentToAddEditScheduledTreatmentFragment(scheduledTreatment.scheduledTreatment.id)
                findNavController().navigate(action)

        })

        // TODO extract to viewmodel
        val database = AppDatabase.getDatabase(requireContext(), lifecycleScope)
        //which upcoming should it choose? upcoming treatments or sms
        database.scheduledTreatmentDao().getUpcomingScheduledTreatmentsWithData(Calendar.getInstance(Locale.getDefault())).observe(viewLifecycleOwner) {

            adapter.setScheduledTreatments(it)
            adapter.notifyDataSetChanged()
        }


        binding.recyclerView.apply {
            this.adapter = adapter
            this.layoutManager = LinearLayoutManager(requireContext())
        }

        return binding.root
    }

}
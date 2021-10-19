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
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentScheduledtreatmentsArchivedBinding
import java.util.*

class ArchivedScheduledTreatmentsFragment: Fragment() {

    private var _binding: FragmentScheduledtreatmentsArchivedBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduledtreatmentsArchivedBinding.inflate(inflater, container, false)

        val adapter = ScheduledTreatmentAdapter()

        val database = AppDatabase.getDatabase(requireContext(), lifecycleScope)
        database.scheduledTreatmentDao().getOldScheduledTreatmentsWithData(Calendar.getInstance()).observe(viewLifecycleOwner) {
            adapter.setScheduledTreatments(it)
            adapter.notifyDataSetChanged()
        }

        adapter.setOnScheduledTreatmentClickedListener(ScheduledTreatmentAdapter.OnScheduledTreatmentClickedListener {position, scheduledTreatment ->
            val action = ScheduledTreatmentsFragmentDirections
                .actionScheduledTreatmentsFragmentToScheduledTreatmentDetailFragment(
                    scheduledTreatment.scheduledTreatment.id)
            findNavController().navigate(action)
        })


        binding.recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        return binding.root
    }
}
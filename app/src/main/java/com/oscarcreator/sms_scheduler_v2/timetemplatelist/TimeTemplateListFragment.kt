package com.oscarcreator.sms_scheduler_v2.timetemplatelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentTimetemplateListBinding

class TimeTemplateListFragment : Fragment() {

    private var _binding: FragmentTimetemplateListBinding? = null

    private val binding: FragmentTimetemplateListBinding
        get() = _binding!!

    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTimetemplateListBinding.inflate(inflater, container, false)

        database = AppDatabase.getDatabase(requireContext(), lifecycleScope)

        val adapter = TimeTemplateAdapter(TimeTemplateAdapter.OnTimeTemplateClickedListener {
            val dialog = TimeTemplateBottomSheetDialogFragment()
            dialog.setOnCompleteButtonClicked {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("timetemplate-id-key", it.id)
                findNavController().popBackStack()
            }
            dialog.arguments = Bundle().apply { putLong("timetemplate-key", it.delay) }
            dialog.show(childFragmentManager, "bottom-sheet-timetemplate")
        })

        database.timeTemplateDao().getTimeTemplates().observe(viewLifecycleOwner) {
            adapter.setTimeTemplates(it)
            adapter.notifyDataSetChanged()
        }

        binding.rvTimetemplateList.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())

        }


        binding.fabAddTimetemplate.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.addEditTimeTemplateFragment))


        return binding.root
    }




}
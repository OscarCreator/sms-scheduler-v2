package com.oscarcreator.sms_scheduler_v2.addedittreatment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.oscarcreator.sms_scheduler_v2.SmsSchedulerApplication
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentAddeditTreatmentBinding

class AddEditTreatmentFragment : Fragment() {

    private val args: AddEditTreatmentFragmentArgs by navArgs()

    private var _binding: FragmentAddeditTreatmentBinding? = null
    private val binding: FragmentAddeditTreatmentBinding
        get() = _binding!!

    private val viewModel by viewModels<AddEditTreatmentViewModel> {
        AddEditTreatmentViewModelFactory(
            (requireContext().applicationContext as SmsSchedulerApplication).treatmentsRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.start(args.treatmentId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddeditTreatmentBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        binding.lifecycleOwner = this.viewLifecycleOwner

        viewModel.treatmentUpdatedEvent.observe(viewLifecycleOwner) {
            findNavController().navigateUp()

        }

        viewModel.snackbarText.observe(viewLifecycleOwner) {

        }

        binding.fabSaveTreatment.setOnClickListener {
            viewModel.saveTreatment()
        }

        return binding.root
    }
}

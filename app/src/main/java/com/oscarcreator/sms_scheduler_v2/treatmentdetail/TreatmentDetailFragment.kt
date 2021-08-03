package com.oscarcreator.sms_scheduler_v2.treatmentdetail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.SmsSchedulerApplication
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentDetailTreatmentBinding
import com.oscarcreator.sms_scheduler_v2.util.EventObserver

class TreatmentDetailFragment : Fragment() {

    private val args: TreatmentDetailFragmentArgs by navArgs()

    private var _binding: FragmentDetailTreatmentBinding? = null
    private val binding: FragmentDetailTreatmentBinding
        get() = _binding!!

    private val viewModel by viewModels<TreatmentDetailViewModel> {
        TreatmentDetailViewModelFactory(
            (requireContext().applicationContext as SmsSchedulerApplication).treatmentsRepository
        )

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                viewModel.deleteTreatment()

            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.treatment_detail_fragment_menu, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetailTreatmentBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        binding.lifecycleOwner = this.viewLifecycleOwner

        viewModel.start(args.treatmentId)

        binding.fabEditTreatment.setOnClickListener {
            val action = TreatmentDetailFragmentDirections
                .actionTreatmentDetailFragmentToAddEditTreatment(args.treatmentId)
            findNavController().navigate(action)
        }

        viewModel.deleteTreatmentEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        setHasOptionsMenu(true)

        return binding.root
    }

}
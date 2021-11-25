package com.oscarcreator.sms_scheduler_v2.addedittreatment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.SmsSchedulerApplication
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentAddeditTreatmentBinding
import com.oscarcreator.sms_scheduler_v2.settings.SettingsFragment
import com.oscarcreator.sms_scheduler_v2.util.EventObserver
import com.oscarcreator.sms_scheduler_v2.util.IntInputFilter
import com.oscarcreator.sms_scheduler_v2.util.setupSnackbar

class AddEditTreatmentFragment : Fragment() {

    private val args: AddEditTreatmentFragmentArgs by navArgs()

    private var _binding: FragmentAddeditTreatmentBinding? = null
    private val binding: FragmentAddeditTreatmentBinding
        get() = _binding!!

    private val viewModel by viewModels<AddEditTreatmentViewModel> {
        AddEditTreatmentViewModelFactory(
            (requireContext().applicationContext as SmsSchedulerApplication).treatmentsRepository)
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

        viewModel.start(args.treatmentId)

        viewModel.treatmentUpdatedEvent.observe(viewLifecycleOwner, EventObserver {
            if (it != -1L) {
                //updated
                val action = AddEditTreatmentFragmentDirections.actionAddEditTreatmentToTreatmentDetailFragment(it)
                findNavController().navigate(action)
            } else {
                //created
                findNavController().navigateUp()
            }

        })

        binding.fabSaveTreatment.setOnClickListener {
            viewModel.saveTreatment()
        }

        binding.etPrice.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                viewModel.saveTreatment()
                true
            }
            false
        }

        binding.tilPrice.suffixText = requireContext().getSharedPreferences(SettingsFragment.SETTINGS_SHARED_PREF, Context.MODE_PRIVATE)
            .getString(SettingsFragment.CURRENCY_TAG, requireContext()
                .getString(R.string.default_currency))

        binding.etPrice.filters = arrayOf(IntInputFilter())
        binding.etDuration.filters = arrayOf(IntInputFilter())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT, binding.fabSaveTreatment)
    }
}

package com.oscarcreator.sms_scheduler_v2.addedittimetemplate

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.SmsSchedulerApplication
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentAddeditTimetemplateBinding
import com.oscarcreator.sms_scheduler_v2.util.EventObserver
import com.oscarcreator.sms_scheduler_v2.util.scheduleAlarm
import com.oscarcreator.sms_scheduler_v2.util.setupSnackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEditTimeTemplateFragment : Fragment() {

    private var _binding: FragmentAddeditTimetemplateBinding? = null

    private val binding: FragmentAddeditTimetemplateBinding
        get() = _binding!!

    private val args: AddEditTimeTemplateFragmentArgs by navArgs()

    private val viewModel by viewModels<AddEditTimeTemplateViewModel> {
        AddEditTimeTemplateViewModelFactory((requireContext().applicationContext as SmsSchedulerApplication).timeTemplatesRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.addedit_timetemplate_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.complete -> {
                viewModel.saveTimeTemplate()
                return true
            }
        }

        return super.onOptionsItemSelected(item)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddeditTimetemplateBinding.inflate(inflater, container, false)

        viewModel.start(args.timetemplateId)

        binding.npDays.apply {
            minValue = 0
            maxValue = 365

            setOnValueChangedListener { _, _, newValue ->
                viewModel.days = newValue
            }
        }

        binding.npHours.apply {
            minValue = 0
            maxValue = 23

            setOnValueChangedListener { _, _, newValue ->
                viewModel.hours = newValue
            }
        }

        binding.npMinutes.apply {
            minValue = 0
            maxValue = 59

            setOnValueChangedListener { _, _, newValue ->
                viewModel.minutes = newValue
            }
        }

        binding.switchBeforeafter.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                binding.switchBeforeafter.text = binding.switchBeforeafter.textOn
            }else{
                binding.switchBeforeafter.text = binding.switchBeforeafter.textOff
            }
            viewModel.switchState = isChecked
        }

        viewModel.timeTemplateLoadedEvent.observe(viewLifecycleOwner, EventObserver {
            binding.npMinutes.value = viewModel.minutes
            binding.npHours.value = viewModel.hours
            binding.npDays.value = viewModel.days
            binding.switchBeforeafter.isChecked = viewModel.switchState
        })

        viewModel.timeTemplateUpdatedEvent.observe(viewLifecycleOwner, EventObserver {
            if (it != -1L) {
                //updated
                lifecycleScope.launch(Dispatchers.IO) {

                    // reschedule scheduled treatments with new timetemplate
                    val scheduledTreatments = viewModel.getScheduledTreatmentsWithTimeTemplateId(it)
                    for (scheduledTreatment in scheduledTreatments) {
                        scheduleAlarm(requireContext(), scheduledTreatment)
                    }

                    withContext(Dispatchers.Main) {
                        val action = AddEditTimeTemplateFragmentDirections.actionAddEditTimeTemplateFragmentToTimeTemplateDetailFragment(it)
                        findNavController().navigate(action)
                    }
                }
            } else {
                // created
                findNavController().navigateUp()
            }
        })

        return binding.root
    }
}
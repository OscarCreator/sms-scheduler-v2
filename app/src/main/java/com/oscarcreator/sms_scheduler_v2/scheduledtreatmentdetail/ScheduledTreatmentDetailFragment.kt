package com.oscarcreator.sms_scheduler_v2.scheduledtreatmentdetail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.SmsSchedulerApplication
import com.oscarcreator.sms_scheduler_v2.data.scheduled.SmsStatus
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentDetailScheduledtreatmentBinding
import com.oscarcreator.sms_scheduler_v2.util.EventObserver
import com.oscarcreator.sms_scheduler_v2.util.toTimeTemplateText
import java.text.SimpleDateFormat
import java.util.*

class ScheduledTreatmentDetailFragment : Fragment() {

    private val args: ScheduledTreatmentDetailFragmentArgs by navArgs()

    private var _binding: FragmentDetailScheduledtreatmentBinding? = null
    private val binding: FragmentDetailScheduledtreatmentBinding
        get() = _binding!!

    private val viewModel by viewModels<ScheduledTreatmentViewModel> {
        ScheduledTreatmentViewModelFactory((requireContext().applicationContext as SmsSchedulerApplication).scheduledTreatmentsRepository)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.scheduled_treatment_detail_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                viewModel.delete(requireContext())
                true
            }

            R.id.send_now -> {
                viewModel.sendNow(requireContext())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailScheduledtreatmentBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        viewModel.start(args.scheduledTreatmentId)


        viewModel.editScheduledTreatmentEvent.observe(viewLifecycleOwner, EventObserver {
            val action = ScheduledTreatmentDetailFragmentDirections
                .actionScheduledTreatmentDetailFragmentToAddEditScheduledTreatmentFragment(args.scheduledTreatmentId)
            findNavController().navigate(action)
        })

        viewModel.deleteScheduledTreatmentEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        viewModel.scheduledTreatment.observe(viewLifecycleOwner) {
            binding.tvContact.text = it.contact.name

            val c = Calendar.getInstance()
            c.timeInMillis = it.scheduledTreatment.treatmentTime.timeInMillis

            val simpleTimeFormat =  SimpleDateFormat("HH:mm dd/MM-yy", Locale.getDefault())

            binding.tvTime.text = simpleTimeFormat.format(c.time)
            binding.tvTreatment.text = it.treatment.name
            binding.tvTimetemplate.text = it.timeTemplate.delay.toTimeTemplateText()
            binding.tvMessagetemplate.text = it.message.message

            binding.fabEditScheduledTreatment.isEnabled = (it.scheduledTreatment.smsStatus == SmsStatus.SCHEDULED)
        }

        binding.fabEditScheduledTreatment.setOnClickListener {
            viewModel.editScheduledTreatment()
        }


        return binding.root
    }


}
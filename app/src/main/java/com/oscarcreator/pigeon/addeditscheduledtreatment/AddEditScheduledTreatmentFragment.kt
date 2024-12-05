package com.oscarcreator.pigeon.addeditscheduledtreatment

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.oscarcreator.pigeon.R
import com.oscarcreator.pigeon.SmsSchedulerApplication
import com.oscarcreator.pigeon.data.contact.Contact
import com.oscarcreator.pigeon.databinding.FragmentAddeditScheduledTreatmentBinding
import com.oscarcreator.pigeon.util.EventObserver
import com.oscarcreator.pigeon.util.setupSnackbar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddEditScheduledTreatmentFragment : Fragment() {

    companion object {
        const val TAG = "AddEditTreatmentFragment"
    }
    
    private val args: AddEditScheduledTreatmentFragmentArgs by navArgs()
    
    private var _binding: FragmentAddeditScheduledTreatmentBinding? = null

    private val binding: FragmentAddeditScheduledTreatmentBinding
        get() = _binding!!

    private val viewModel by viewModels<AddEditScheduledTreatmentViewModel> {
        AddEditScheduledTreatmentViewModelFactory(
            (requireContext().applicationContext as SmsSchedulerApplication).contactsRepository,
            (requireContext().applicationContext as SmsSchedulerApplication).treatmentsRepository,
            (requireContext().applicationContext as SmsSchedulerApplication).timeTemplatesRepository,
            (requireContext().applicationContext as SmsSchedulerApplication).messagesRepository,
            (requireContext().applicationContext as SmsSchedulerApplication).scheduledTreatmentsRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.addedit_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menu_check_done -> {
                viewModel.saveScheduledTreatment(requireActivity())
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setupSnackbar(viewLifecycleOwner, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddeditScheduledTreatmentBinding.inflate(inflater, container, false)

        viewModel.scheduledTreatmentUpdatedEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        viewModel.start(if (args.scheduledTreatmentId == -1L) null else args.scheduledTreatmentId, requireContext())

        viewModel.contact.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.flContacts.apply {
                    if (childCount > 1) {
                        binding.flContacts.removeViewAt(binding.flContacts.childCount - 2)
                    }
                }
                addNewChip(it)

                binding.btnContact.visibility = View.INVISIBLE

            } else {
                binding.flContacts.apply {
                    if (childCount > 1) {
                        binding.flContacts.removeViewAt(binding.flContacts.childCount - 2)
                    }
                    binding.btnContact.visibility = View.VISIBLE
                }
            }

        }

        binding.btnTreatment.setOnClickListener {
            val action = AddEditScheduledTreatmentFragmentDirections.actionAddEditScheduledTreatmentFragmentToTreatmentsFragment()
            findNavController().navigate(action)
        }

        val formatter = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
        viewModel.time.observe(viewLifecycleOwner, {
                binding.btnTime.text = formatter.format(it)
            }
        )

        //TODO move to functions
        //TODO dynamically set time format
        binding.btnTime.setOnClickListener {
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(Calendar.getInstance(Locale.getDefault()).get(Calendar.HOUR_OF_DAY))
                .build().apply {
                    addOnPositiveButtonClickListener {

                        Log.d(TAG, "launching date picker")
                        MaterialDatePicker.Builder.datePicker()
                            .build().apply {
                                addOnPositiveButtonClickListener {
                                    //adding date as millisecond to the chosen time
                                    val calendar = Calendar.getInstance(Locale.getDefault())
                                    calendar.timeInMillis = it
                                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                                    calendar.set(Calendar.MINUTE, minute)

                                    viewModel.time.value = calendar.timeInMillis

                                }
                            }
                            .show(this@AddEditScheduledTreatmentFragment.childFragmentManager, "date-picker")
                    }
                }.show(childFragmentManager, "time-picker")
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>("message_id")?.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                viewModel.setMessageById(it)
            }

        }

        //TODO action not working with navigation test
        binding.btnMessage.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.messageListFragment))

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>("timetemplate-id-key")?.observe(viewLifecycleOwner, {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.setTimeTemplateById(it)
            }
        })

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>("treatment_id")?.observe(viewLifecycleOwner, {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.setTreatmentById(it)
            }
        })

        binding.btnContact.setOnClickListener {
            val action = AddEditScheduledTreatmentFragmentDirections.actionAddEditScheduledTreatmentFragmentToContactListFragment()
            findNavController().navigate(action)

        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>("contact_id")?.observe(viewLifecycleOwner, {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.setContactById(it)
            }
        })

        viewModel.message.observe(viewLifecycleOwner, {
            binding.btnMessage.text = it
        })

        viewModel.timeTemplateText.observe(viewLifecycleOwner, {
            binding.btnTimetemplate.text = it

        })

        viewModel.treatment.observe(viewLifecycleOwner, {
            binding.btnTreatment.text = it?.name
        })

        binding.btnTimetemplate.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.timeTemplateListFragment))

        setUpSendSmsPermission()

        return binding.root
    }

    private fun setUpSendSmsPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    AlertDialog.Builder(context)
                        .setTitle("Send Sms Permission required")
                        .setMessage("Send sms permission is required to scheduled sms to be sent automatically")
                        .setPositiveButton("Ok") { _, _ ->
                            findNavController().navigateUp()
                        }
                        .create().show()
                    Log.d(TAG, "explain feature will be unavailable")

                }
            }


        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is granted
            }
            //TODO extract text to strings.xml
            shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                AlertDialog.Builder(context)
                    .setTitle("Sms permission required")
                    .setMessage("Send sms permission is required to scheduled sms to be sent automatically. Do you want to allow Send SMS permission?")
                    .setPositiveButton("Yes") {_, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                    }
                    .setNegativeButton("No") { _, _ ->
                        findNavController().navigateUp()
                    }.create().show()
                Log.d(TAG, "show in context UI")
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
            }
        }
    }

    private fun addNewChip(contact: Contact) {
        val chip = Chip(context).apply {
            text = contact.name
            isCloseIconVisible = true
            isClickable = true
            isCheckable = false
            setOnCloseIconClickListener {
                findNavController().getBackStackEntry(R.id.addEditScheduledTreatmentFragment).savedStateHandle.set("contact_id", -1L)
                viewModel.removeContact()
            }
            setOnClickListener {
                findNavController().getBackStackEntry(R.id.addEditScheduledTreatmentFragment).savedStateHandle.set("contact_id", -1L)
                viewModel.removeContact()
            }
        }

        binding.flContacts.addView(chip, binding.flContacts.childCount - 1)
    }


}
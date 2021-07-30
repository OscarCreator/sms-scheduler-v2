package com.oscarcreator.sms_scheduler_v2.addeditscheduledtreatment

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.SmsSchedulerApplication
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentAddeditScheduledTreatmentBinding
import com.oscarcreator.sms_scheduler_v2.util.EventObserver
import com.oscarcreator.sms_scheduler_v2.util.toTimeTemplateText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

//TODO only be able to send one customer at a time. Maybe split them up to multiple scheduled treatments instead.
// Otherwise it will be difficult to keep track of status of the sms
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
            (requireContext().applicationContext as SmsSchedulerApplication).customersRepository,
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
            //TODO remove?
            R.id.menu_clear -> {
                //viewModel.clearData()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        binding.tvTreatments.setText(viewModel.treatment.value?.name, false)

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

        //TODO arguments
        viewModel.start(if (args.scheduledTreatmentId == -1L) null else args.scheduledTreatmentId)

        //TODO replace with a custom adapter
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu_list_item, mutableListOf<String>())
        viewModel.allTreatment.observe(viewLifecycleOwner, {
            adapter.clear()
            for (treatment in it){
                adapter.add(treatment.name)
            }
        })
        binding.tvTreatments.setAdapter(adapter)
        binding.tvTreatments.setOnItemClickListener { _, _, position, _ ->
            viewModel.treatment.value = viewModel.allTreatment.value?.get(position)
        }

        //adapter.getPosition(binding.tvTreatments.text.toString())

        viewModel.treatment.observe(viewLifecycleOwner, {
            binding.tvTreatments.setText(it.name)

        })

        val formatter = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
        viewModel.time.observe(viewLifecycleOwner, {
                binding.btnTime.text = formatter.format(it)
            }
        )

        //TODO move to functions
        binding.btnTime.setOnClickListener {
            MaterialTimePicker.Builder()
                .setHour(9)
                .build().apply {
                    addOnPositiveButtonClickListener {
                        val parser = SimpleDateFormat("HH:mm", Locale.getDefault())
                        val timeString = "$hour:$minute"
                        val date = parser.parse(timeString)
                        var chosenTime = date!!.time

                        Log.d(TAG, "launching date picker")
                        MaterialDatePicker.Builder.datePicker()
                            .build().apply {
                                addOnPositiveButtonClickListener {
                                    //adding date as millisecond to the chosen time
                                    chosenTime += it
                                    viewModel.time.value = chosenTime

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

        //TODO exception in test "does not have a NavController set"
//        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>("key")?.observe(viewLifecycleOwner, {
//            viewLifecycleOwner.lifecycleScope.launch {
//                addEditScheduledTreatmentViewModel.message.value = database.messageDao().observeMessage(it).value
//            }
//        })

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>("timetemplate-id-key")?.observe(viewLifecycleOwner, {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.setTimeTemplateById(it)
            }
        })

        viewModel.message.observe(viewLifecycleOwner, {
            binding.btnMessage.text = it.message
        })

        viewModel.timeModifier.observe(viewLifecycleOwner, {
            binding.btnTimetemplate.text = it.delay.toTimeTemplateText()

        })

        binding.btnTimetemplate.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.timeTemplateListFragment))

        setUpContactInput()

        viewModel.customersLoadedEvent.observe(viewLifecycleOwner, EventObserver {
            for (receiver in viewModel.customers) {
                addNewChip(receiver)
            }
        })

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

    //TODO refactor flexboxlayout with editext to a view
    private fun setUpContactInput() {

        val adapter = ContactsListAdapter(
            ContactsListAdapter.OnContactClickedListener { customer: Customer ->
                addReceiver(customer)
            }
        )

        binding.rvAutocompleteList.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)

        }

        binding.etContactInput.apply {
            setOnEditorActionListener { _, keyCode, _ ->
                // if ime action is clicked
                if (keyCode == EditorInfo.IME_ACTION_DONE) {

                    //If only one item left in recyclerview add the customer
                    if (binding.rvAutocompleteList.visibility == View.VISIBLE &&
                        adapter.itemCount == 1) {
                        addReceiver(adapter.list[0])
                        return@setOnEditorActionListener true
                    }
                }
                false
            }
            addTextChangedListener(
                afterTextChanged = { text ->
                    // hide if there is no text, otherwise show and replace adapter list
                    if (text != null && text.isEmpty()) {
                        binding.rvAutocompleteList.visibility = View.GONE
                    } else {
                        lifecycleScope.launch {
                            adapter.setContacts(
                                viewModel.getCustomersLike(text.toString()))
                            binding.rvAutocompleteList.visibility = View.VISIBLE
                        }
                    }
                })

            //remove chip if hitting del
            setOnKeyListener { _, keyCode, keyEvent ->
                if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    removeReceiver()
                    return@setOnKeyListener true
                }

                false
            }

            // Remove the ability to write a space as first character
            filters = arrayOf(
                InputFilter { source, _, _, _, dstart, _ ->
                    if (source == " " && dstart == 0) {
                        return@InputFilter ""
                    }
                    null
                })
        }
    }

    private fun addReceiver(customer: Customer) {
        binding.etContactInput.text.clear()
        addNewChip(customer)
        viewModel.addReceiver(customer)
    }

    private fun removeReceiver(){
        binding.flContacts.run {
            if (childCount > 1) {
                viewModel.removeReceiver(childCount - 2)
                removeViewAt(childCount - 2)
            }
        }
    }

    private fun addNewChip(customer: Customer) {
        val chip = Chip(context).apply {
            text = customer.name
            isCloseIconVisible = true
            isClickable = true
            isCheckable = false
            setOnCloseIconClickListener {
                binding.flContacts.removeView(this)
                //better to delete with index
                viewModel.removeReceiver(customer)
            }
        }

        binding.flContacts.addView(chip, binding.flContacts.childCount - 1)
    }


}
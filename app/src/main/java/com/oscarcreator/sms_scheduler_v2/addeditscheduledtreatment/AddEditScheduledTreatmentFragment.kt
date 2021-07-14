package com.oscarcreator.sms_scheduler_v2.addeditscheduledtreatment

import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.SmsSchedulerApplication
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentAddeditScheduledTreatmentBinding
import com.oscarcreator.sms_scheduler_v2.util.toTimeTemplateText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddEditScheduledTreatmentFragment : Fragment() {

    companion object {
        const val TAG = "AddEditTreatmentFragment"
    }

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
                viewModel.saveScheduledTreatment()
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

        for (receiver in viewModel.customers) {
            addNewChip(receiver)
        }

        binding.tvTreatments.setText(viewModel.treatment.value?.name, false)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddeditScheduledTreatmentBinding.inflate(inflater, container, false)

        viewModel.scheduledTreatmentUpdatedEvent.observe(viewLifecycleOwner, {
            findNavController().navigateUp()
        })

        //TODO arguments
        viewModel.start()

        //TODO replace with a custom adapter
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu_list_item, mutableListOf<String>())
        viewModel.allTreatment.observe(viewLifecycleOwner, {
            for (treatment in it){
                adapter.add(treatment.name)
            }
        })
        binding.tvTreatments.setAdapter(adapter)
        binding.tvTreatments.setOnItemClickListener { _, _, position, _ ->
            viewModel.treatment.value = viewModel.allTreatment.value?.get(position)
        }

        adapter.getPosition(binding.tvTreatments.text.toString())

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

        return binding.root
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
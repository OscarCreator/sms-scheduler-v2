package com.oscarcreator.sms_scheduler_v2.addeditscheduledtreatment

import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.data.customer.CustomerRepository
import com.oscarcreator.sms_scheduler_v2.data.scheduled.DefaultScheduledTreatmentRepository
import com.oscarcreator.sms_scheduler_v2.data.treatment.TreatmentRepository
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentAddeditTreatmentBinding
import com.oscarcreator.sms_scheduler_v2.util.toTimeTemplateText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddEditScheduledTreatmentFragment : Fragment() {

    companion object {
        const val TAG = "AddEditTreatmentFragment"
    }

    private var _binding: FragmentAddeditTreatmentBinding? = null

    private val binding: FragmentAddeditTreatmentBinding
        get() = _binding!!

    private lateinit var addEditScheduledTreatmentViewModel: AddEditScheduledTreatmentViewModel

    private lateinit var database: AppDatabase

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
                //addEditTreatmentViewModel.saveScheduledTreatment()
            }
            R.id.menu_clear -> {
                //clear
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddeditTreatmentBinding.inflate(inflater, container, false)

        database = AppDatabase.getDatabase(requireContext(), lifecycleScope)
        //TODO move to injectorUtils?
        addEditScheduledTreatmentViewModel = AddEditScheduledTreatmentViewModel(
            CustomerRepository.getInstance(database.customerDao()),
            DefaultScheduledTreatmentRepository.getInstance(
                database.scheduledTreatmentDao(),
                database.scheduledTreatmentCrossRefDao()
            )
        )

        val treatmentRepository = TreatmentRepository.getInstance(database.treatmentDao())

        addEditScheduledTreatmentViewModel.start()

        //TODO replace with a custom adapter
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu_list_item, mutableListOf<String>())
        treatmentRepository.getTreatments().observe(viewLifecycleOwner, {
            for (treatment in it){
                adapter.add(treatment.name)
            }
        })
        binding.tvTreatments.setAdapter(adapter)


        val formatter = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
        addEditScheduledTreatmentViewModel.time.observe(viewLifecycleOwner, {
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
                                    addEditScheduledTreatmentViewModel.time.value = chosenTime

                                }
                            }
                            .show(this@AddEditScheduledTreatmentFragment.childFragmentManager, "date-picker")
                    }
                }.show(childFragmentManager, "time-picker")
        }

        //TODO action not working with navigation test
        binding.btnMessage.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.messageListFragment))

        //TODO exception in test "does not have a NavController set"
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>("key")?.observe(viewLifecycleOwner, {
            viewLifecycleOwner.lifecycleScope.launch {
                addEditScheduledTreatmentViewModel.message.value = database.messageDao().getMessage(it)
            }
        })

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Long>("timetemplate-id-key")?.observe(viewLifecycleOwner, {
            viewLifecycleOwner.lifecycleScope.launch {
                addEditScheduledTreatmentViewModel.timeModifier.value = database.timeTemplateDao().getTimeTemplate(it)
            }
        })

        addEditScheduledTreatmentViewModel.message.observe(viewLifecycleOwner, {
            binding.btnMessage.text = it.message
        })

        addEditScheduledTreatmentViewModel.timeModifier.observe(viewLifecycleOwner, {
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
                                addEditScheduledTreatmentViewModel.getCustomersLike(text.toString()))
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
        addEditScheduledTreatmentViewModel.addReceiver(customer)
    }

    private fun removeReceiver(){
        binding.flContacts.run {
            if (childCount > 1) {
                addEditScheduledTreatmentViewModel.removeReceiver(childCount - 2)
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
                addEditScheduledTreatmentViewModel.removeReceiver(customer)
            }
        }

        binding.flContacts.addView(chip, binding.flContacts.childCount - 1)
    }


}
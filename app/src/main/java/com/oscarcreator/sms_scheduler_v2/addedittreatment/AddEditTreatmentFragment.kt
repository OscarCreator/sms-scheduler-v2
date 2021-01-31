package com.oscarcreator.sms_scheduler_v2.addedittreatment

import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.data.scheduled.DefaultScheduledTreatmentRepository
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentAddeditTreatmentBinding
import java.text.SimpleDateFormat
import java.util.*

class AddEditTreatmentFragment : Fragment() {

    companion object {
        const val TAG = "AddEditTreatmentFragment"
    }

    var _binding: FragmentAddeditTreatmentBinding? = null

    val binding: FragmentAddeditTreatmentBinding
        get() = _binding!!

    lateinit var addEditTreatmentViewModel: AddEditTreatmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddeditTreatmentBinding.inflate(inflater, container, false)

        val database = AppDatabase.getDatabase(requireContext(), lifecycleScope)
        addEditTreatmentViewModel = AddEditTreatmentViewModel(
            DefaultScheduledTreatmentRepository.getInstance(
                database.scheduledTreatmentDao(),
                database.scheduledTreatmentCrossRefDao()
            )
        )

        addEditTreatmentViewModel.start()

        //TODO replace with a custom adapter
        val tempItems = listOf("Behandling 1", "Behandling 2", "Behandling 3")
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu_list_item, tempItems)
        binding.tvTreatments.setAdapter(adapter)


        val formatter = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
        addEditTreatmentViewModel.time.observe(viewLifecycleOwner, {
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
                                    addEditTreatmentViewModel.time.value = chosenTime

                                }
                            }
                            .show(this@AddEditTreatmentFragment.childFragmentManager, "date-picker")
                    }
                }.show(childFragmentManager, "time-picker")
        }


        binding.btnMessage.setOnClickListener {
            Toast.makeText(requireContext(), "Message button clicked", Toast.LENGTH_LONG)
                .show()
        }

        binding.btnTimetemplate.setOnClickListener {
            Toast.makeText(requireContext(), "Time template button clicked", Toast.LENGTH_LONG)
                .show()
        }

        setUpContactInput()
        setUpAutocompleteList()

        return binding.root
    }

    private fun setUpAutocompleteList() {
        //TODO replace with contact on device
        // this list is updated with a query every time the text changes
        // so only the matching contacts is shown
        val tempList = listOf(
            Pair("Bengt Bengtsson", "074502352"),
            Pair("Bengt Bengtsson2", "074502352"),
            Pair("Bengt Bengtsson3", "074502352"),
            Pair("Bengt Bengtsson4", "074502352"),
            Pair("Bengt Bengtsson5", "074502352"),
            Pair("Bengt Bengtsson6", "074502352")
        )

        val adapter = ContactsListAdapter(
            ContactsListAdapter.OnContactClickedListener { name: String ->
                binding.etContactInput.setText("$name ")
            }
        ).apply {
            setContactList(tempList)
        }

        binding.rvAutocompleteList.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)

        }
    }

    //TODO refactor flexboxlayout with editext to a view
    //TODO make only recyclerview able to add receiver and not space or ime action
    private fun setUpContactInput() {
        binding.etContactInput.apply {
            setOnEditorActionListener { textView, keyCode, _ ->
                // if ime action is clicked
                if (keyCode == EditorInfo.IME_ACTION_DONE) {
                    binding.rvAutocompleteList.visibility = View.GONE

                    //and text is greater than 1
                    if (textView.text.isNotEmpty()) {
                        addReceiver()
                        return@setOnEditorActionListener true
                    }

                }
                false
            }

            setOnKeyListener { view, keyCode, keyEvent ->
                if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    removeChip()
                    return@setOnKeyListener true
                }

                false
            }
            addTextChangedListener(onTextChanged = { text, _, _, _ ->
                if (text != null) {
                    if (text.length > 1 && text[text.lastIndex] == ' ') {

                        binding.rvAutocompleteList.visibility = View.GONE
                        addReceiver()
                    } else if (text.isEmpty()) {
                        binding.rvAutocompleteList.visibility = View.GONE
                    } else {
                        binding.rvAutocompleteList.visibility = View.VISIBLE
                    }
                }
            })

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

    private fun addReceiver(
        name: String = binding.etContactInput.text.toString().trim()
    ) {
        binding.etContactInput.text.clear()
        addNewChip(name)
    }

    private fun addNewChip(person: String) {
        //TODO add ... at end if not able display everyting
        val chip = Chip(context).apply {
            text = person
            isCloseIconVisible = true
            isClickable = true
            isCheckable = false
            setOnCloseIconClickListener {
                binding.flContacts.removeView(this)
            }
        }

        binding.flContacts.addView(chip, binding.flContacts.childCount - 1)
    }

    private fun removeChip() {
        binding.flContacts.run {
            if (childCount > 1) {
                removeViewAt(childCount - 2)
            }
        }
    }

}
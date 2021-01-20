package com.oscarcreator.sms_scheduler_v2.addedittreatment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.oscarcreator.sms_scheduler_v2.R
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

    //TODO move to viewmodel
    var chosenTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddeditTreatmentBinding.inflate(inflater, container, false)

        val tempItems = listOf("Behandling 1", "Behandling 2", "Behandling 3")
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu_list_item, tempItems)
        binding.tvTreatments.setAdapter(adapter)

        //TODO move to functions
        binding.btnTime.setOnClickListener {
            MaterialTimePicker.Builder()
                .setHour(9)
                .build().apply {
                    addOnPositiveButtonClickListener {
                        val parser = SimpleDateFormat("HH:mm", Locale.getDefault())
                        val timeString = "$hour:$minute"
                        val date = parser.parse(timeString)
                        chosenTime = date!!.time

                        Log.d(TAG, "launching date picker")
                        MaterialDatePicker.Builder.datePicker()
                            //TODO smart selection
                            .setTitleText("Title")
                            .build().apply {
                                addOnPositiveButtonClickListener {
                                    //adding date as millisecond to the chosen time
                                    chosenTime += it

                                    //TODO move to an observer
                                    val formater = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
                                    binding.btnTime.text = formater.format(chosenTime)
                                }

                            }.show(this@AddEditTreatmentFragment.childFragmentManager, "date-picker")

                    }
                }.show(childFragmentManager, "time-picker")
        }


        binding.btnMessage.setOnClickListener {
            Toast.makeText(requireContext(), "Message button clicked", Toast.LENGTH_LONG).show()
        }

        binding.btnTimetemplate.setOnClickListener {
            Toast.makeText(requireContext(), "Time template button clicked", Toast.LENGTH_LONG).show()
        }

        return binding.root
    }

}
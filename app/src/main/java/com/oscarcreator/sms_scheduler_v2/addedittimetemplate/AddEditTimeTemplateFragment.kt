package com.oscarcreator.sms_scheduler_v2.addedittimetemplate

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentAddeditTimetemplateBinding
import kotlinx.coroutines.launch

class AddEditTimeTemplateFragment : Fragment() {

    private var _binding: FragmentAddeditTimetemplateBinding? = null

    private val binding: FragmentAddeditTimetemplateBinding
        get() = _binding!!

    private lateinit var database: AppDatabase

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
                saveTimeTemplate()
                return true
            }
        }

        return super.onOptionsItemSelected(item)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddeditTimetemplateBinding.inflate(inflater, container, false)

        database = AppDatabase.getDatabase(requireContext(), lifecycleScope)

        binding.npDays.minValue = 0
        binding.npDays.maxValue = 365

        binding.npHours.minValue = 0
        binding.npHours.maxValue = 23

        binding.npMinutes.minValue = 0
        binding.npMinutes.maxValue = 59

        binding.switchBeforeafter.setOnClickListener {
            if (binding.switchBeforeafter.isChecked){
                binding.switchBeforeafter.text = binding.switchBeforeafter.textOn
            }else{
                binding.switchBeforeafter.text = binding.switchBeforeafter.textOff
            }

        }


        return binding.root
    }

    private fun validateTimeTemplate(): Boolean {
        if (binding.npDays.value == 0 &&
            binding.npHours.value == 0 &&
            binding.npMinutes.value == 0){
            Toast.makeText(requireContext(), getString(R.string.time_template_missing_data), Toast.LENGTH_LONG)
                .show()
            return false
        }
        return true
    }

    private fun saveTimeTemplate(){
        if (validateTimeTemplate()){
            lifecycleScope.launch {
                database.timeTemplateDao().insert(TimeTemplate(delay = getTimeInMillis()))
            }
            findNavController().popBackStack()
        }
    }

    private fun getTimeInMillis(): Long {
        return (binding.npDays.value * 24L * 60L * 60L * 1000L +
                binding.npHours.value * 60L * 60L * 1000L +
                binding.npMinutes.value * 60L * 1000L) *
                if(binding.switchBeforeafter.isChecked) -1 else 1
    }

}
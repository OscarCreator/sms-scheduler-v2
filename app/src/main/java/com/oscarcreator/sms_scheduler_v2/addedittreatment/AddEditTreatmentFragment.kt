package com.oscarcreator.sms_scheduler_v2.addedittreatment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentAddeditTreatmentBinding
import kotlinx.coroutines.launch

class AddEditTreatmentFragment : Fragment() {

    private var _binding: FragmentAddeditTreatmentBinding? = null
    private val binding: FragmentAddeditTreatmentBinding
        get() = _binding!!

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.addedit_treatment_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.complete -> {
                saveTreatment()
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


        return binding.root
    }


    private fun validTreatment(): Boolean {
        if (binding.tvTreatmentName.text?.isNotEmpty() == true &&
            binding.tvDuration.text?.isNotEmpty() == true &&
            binding.tvPrice.text?.isNotEmpty() == true){
            return true
        }
        return false
    }

    private fun saveTreatment() {
        if (validTreatment()){
            val name = binding.tvTreatmentName.text.toString()
            val duration = binding.tvDuration.text.toString().toInt()
            val price = binding.tvPrice.text.toString().toInt()


            lifecycleScope.launch {
                database.treatmentDao().insert(
                    Treatment(name = name, duration = duration, price = price))
                //TODO refactor to a observation of the insert in a viewmodel like arch-sample
                //Navigate back only after treatment is inserted
                findNavController().popBackStack()
            }

        }else{
            Toast.makeText(requireContext(), "Missing data", Toast.LENGTH_SHORT).show()
        }
    }
}

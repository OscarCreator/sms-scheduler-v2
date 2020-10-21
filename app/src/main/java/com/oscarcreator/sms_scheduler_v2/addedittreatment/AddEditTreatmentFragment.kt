package com.oscarcreator.sms_scheduler_v2.addedittreatment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentAddeditTreatmentBinding

class AddEditTreatmentFragment : Fragment() {

    var _binding: FragmentAddeditTreatmentBinding? = null

    val binding: FragmentAddeditTreatmentBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddeditTreatmentBinding.inflate(inflater, container, false)

        val tempItems = listOf("Behandling 1", "Behandling 2", "Behandling 3")

        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu_list_item, tempItems)

        binding.acTreatments.setAdapter(adapter)


        return binding.root
    }

}
package com.oscarcreator.sms_scheduler_v2.addeditcontact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.oscarcreator.sms_scheduler_v2.SmsSchedulerApplication
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentAddeditContactBinding
import com.oscarcreator.sms_scheduler_v2.util.EventObserver

class AddEditContactFragment : Fragment() {

    private val args: AddEditContactFragmentArgs by navArgs()

    private var _binding: FragmentAddeditContactBinding? = null
    private val binding: FragmentAddeditContactBinding
        get() = _binding!!

    private val viewModel by viewModels<AddEditContactViewModel> {
        AddEditContactViewModelFactory(
            (requireContext().applicationContext as SmsSchedulerApplication).customersRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddeditContactBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.start(args.contactId)

        viewModel.contactUpdatedEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        binding.fabSaveContact.setOnClickListener {
            viewModel.saveContact()
        }

        return binding.root
    }
}
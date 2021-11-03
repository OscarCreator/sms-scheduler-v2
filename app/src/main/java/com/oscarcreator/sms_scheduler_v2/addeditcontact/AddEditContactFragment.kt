package com.oscarcreator.sms_scheduler_v2.addeditcontact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.oscarcreator.sms_scheduler_v2.R
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
            (requireContext().applicationContext as SmsSchedulerApplication).contactsRepository
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
            if (it != -1L) {
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), R.string.temp_delete_exception_text, Toast.LENGTH_LONG).show()
            }
        })

        binding.fabSaveContact.setOnClickListener {
            viewModel.saveContact()
        }

        binding.etMoney.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                viewModel.saveContact()
                true
            }
            false
        }

        return binding.root
    }
}
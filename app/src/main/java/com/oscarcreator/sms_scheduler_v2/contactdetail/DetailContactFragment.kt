package com.oscarcreator.sms_scheduler_v2.contactdetail

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.SmsSchedulerApplication
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentDetailContactBinding
import com.oscarcreator.sms_scheduler_v2.util.EventObserver

class DetailContactFragment : Fragment() {

    private val args: DetailContactFragmentArgs by navArgs()

    private var _binding: FragmentDetailContactBinding? = null
    val binding: FragmentDetailContactBinding
        get() = _binding!!

    private val viewModel by viewModels<DetailContactViewModel> {
        DetailContactViewModelFactory(
            (requireContext().applicationContext as SmsSchedulerApplication).contactsRepository
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contact_detail_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.delete -> {
                //TODO redo for the possibility to remove contacts
                viewModel.deleteContact {
                    Toast.makeText(requireContext(), getString(R.string.temp_delete_exception_text), Toast.LENGTH_LONG).show()
                }
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
        _binding = FragmentDetailContactBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        binding.lifecycleOwner = viewLifecycleOwner

        setHasOptionsMenu(true)

        viewModel.start(args.contactId)

        viewModel.deleteContactEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        binding.fabEditContact.setOnClickListener {
            val action = DetailContactFragmentDirections
                .actionDetailContactFragmentToAddEditContactFragment(args.contactId)
            findNavController().navigate(action)
        }

        return binding.root
    }

}
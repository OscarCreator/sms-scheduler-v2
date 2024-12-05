package com.oscarcreator.pigeon.contactdetail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.oscarcreator.pigeon.R
import com.oscarcreator.pigeon.SmsSchedulerApplication
import com.oscarcreator.pigeon.databinding.FragmentDetailContactBinding
import com.oscarcreator.pigeon.util.EventObserver
import com.oscarcreator.pigeon.util.setupSnackbar

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
                viewModel.deleteContact()
                return true
            }

            R.id.edit -> {
                viewModel.editContact()
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

        binding.efabChooseContact.setOnClickListener {
            findNavController().popBackStack(R.id.addEditScheduledTreatmentFragment, false)
            findNavController().getBackStackEntry(R.id.addEditScheduledTreatmentFragment).savedStateHandle.set("contact_id", args.contactId)
        }

        viewModel.editContactEvent.observe(viewLifecycleOwner, EventObserver {
            val action = DetailContactFragmentDirections
                .actionDetailContactFragmentToAddEditContactFragment(args.contactId)
            findNavController().navigate(action)
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT, binding.efabChooseContact)
    }

}
package com.oscarcreator.pigeon.addeditmessage

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.oscarcreator.pigeon.R
import com.oscarcreator.pigeon.SmsSchedulerApplication
import com.oscarcreator.pigeon.databinding.FragmentAddeditMessageBinding
import com.oscarcreator.pigeon.util.EventObserver
import com.oscarcreator.pigeon.util.scheduleAlarm
import com.oscarcreator.pigeon.util.setupSnackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEditMessageFragment : Fragment() {

    private val args: AddEditMessageFragmentArgs by navArgs()

    private var _binding: FragmentAddeditMessageBinding? = null

    private val binding: FragmentAddeditMessageBinding
        get() = _binding!!

    private val viewModel by viewModels<AddEditMessageViewModel> {
        AddEditMessageViewModelFactory((requireContext().applicationContext as SmsSchedulerApplication).messagesRepository)
    }

    private fun setupNavigation(){
        viewModel.messageUpdateEvent.observe(viewLifecycleOwner, EventObserver {

            if (it != -1L) {
                // updated
                //TODO loading animation ??
                lifecycleScope.launch(Dispatchers.IO) {

                    // reschedule scheduled treatments with new message
                    val scheduledTreatments = viewModel.getScheduledTreatmentsWithMessageId(it)
                    for (scheduledTreatment in scheduledTreatments) {
                        scheduleAlarm(requireContext(), scheduledTreatment)
                    }

                    withContext(Dispatchers.Main) {
                        val action = AddEditMessageFragmentDirections.actionAddEditMessageFragmentToMessageDetailFragment(it)
                        findNavController().navigate(action)
                    }
                }

            } else {
                // created
                findNavController().navigateUp()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.addedit_message_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.info_variables -> {
                //TODO navigate
                val action = AddEditMessageFragmentDirections.actionAddEditMessageFragmentToVariableInfoFragment()
                findNavController().navigate(action)
                return true
            }

            android.R.id.home -> {
                binding.etMessage.clearFocus()
            }
        }
        return super.onOptionsItemSelected(item)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddeditMessageBinding.inflate(layoutInflater, container, false)

        viewModel.message.observe(viewLifecycleOwner, {
            binding.etMessage.setText(it)
        })

        binding.fabSaveMessage.setOnClickListener {
            viewModel.message.value = binding.etMessage.text.toString()
            viewModel.saveMessage()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        viewModel.start(args.messageId)
        view.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT, binding.fabSaveMessage)
    }
}
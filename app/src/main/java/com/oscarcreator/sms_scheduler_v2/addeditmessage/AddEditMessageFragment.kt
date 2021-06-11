package com.oscarcreator.sms_scheduler_v2.addeditmessage

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.SmsSchedulerApplication
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentAddeditMessageBinding
import com.oscarcreator.sms_scheduler_v2.util.EventObserver

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
            //close keyboard
            binding.etMessage.clearFocus()
            findNavController().navigateUp()
//
//            val action = AddEditMessageFragmentDirections
//                .actionAddEditMessageFragmentToMessageDetailFragment(args.messageId)
//            findNavController().navigate(action)
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
            R.id.complete -> {
                //TODO this could be removed with databinding
                viewModel.message.value = binding.etMessage.text.toString()
                viewModel.saveMessage()
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        viewModel.start(args.messageId)
    }
}
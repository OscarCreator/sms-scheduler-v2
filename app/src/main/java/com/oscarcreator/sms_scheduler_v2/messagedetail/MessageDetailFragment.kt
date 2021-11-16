package com.oscarcreator.sms_scheduler_v2.messagedetail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.SmsSchedulerApplication
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentMessageDetailBinding
import com.oscarcreator.sms_scheduler_v2.util.EventObserver
import com.oscarcreator.sms_scheduler_v2.util.setupSnackbar

class MessageDetailFragment : Fragment() {

    private val args: MessageDetailFragmentArgs by navArgs()

    private var _binding: FragmentMessageDetailBinding? = null
    private val binding: FragmentMessageDetailBinding
        get() = _binding!!

    private val viewModel by viewModels<MessageDetailViewModel> {
        MessageDetailViewModelFactory((requireContext().applicationContext as SmsSchedulerApplication).messagesRepository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        view.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT, binding.efabChoose)
    }

    private fun setupNavigation() {
        viewModel.editMessageEvent.observe(viewLifecycleOwner, EventObserver {
            val action = MessageDetailFragmentDirections.actionMessageDetailFragmentToAddEditMessageFragment(args.messageId)
            findNavController().navigate(action)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.message_detail_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.edit -> {
                viewModel.editMessage()

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
        _binding = FragmentMessageDetailBinding.inflate(inflater, container, false)

        viewModel.message.observe(viewLifecycleOwner, {
            binding.tvMessage.text = it?.message
        })

        viewModel.start(args.messageId)


        binding.efabChoose.setOnClickListener {
            //TODO navigate back to addedit scheduled treatment
            findNavController().popBackStack(R.id.addEditScheduledTreatmentFragment, false)
            findNavController().getBackStackEntry(R.id.addEditScheduledTreatmentFragment).savedStateHandle.set("message_id", args.messageId)
        }

        return binding.root
    }
}
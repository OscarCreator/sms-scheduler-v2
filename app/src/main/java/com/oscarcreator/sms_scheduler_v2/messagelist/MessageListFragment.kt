package com.oscarcreator.sms_scheduler_v2.messagelist

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentMessageListBinding

class MessageListFragment : Fragment() {

    private var _binding: FragmentMessageListBinding? = null

    private val binding: FragmentMessageListBinding
        get() = _binding!!

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageListBinding.inflate(inflater, container, false)

        database = AppDatabase.getDatabase(requireContext(), lifecycleScope)

        val adapter = MessageAdapter(MessageAdapter.OnMessageClickedListener {

            val dialog = MessageBottomSheetDialogFragment()
            dialog.setOnCompleteButtonClicked {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("key", it.id)
                findNavController().popBackStack()
            }
            dialog.arguments = Bundle().apply { putString("message-key", it.message) }
            dialog.show(childFragmentManager, "bottom-sheet")
        })

        database.messageDao().getMessageTemplates().observe(viewLifecycleOwner, {
            adapter.setMessages(it)
            adapter.notifyDataSetChanged()
        })

        binding.rvMessageList.adapter = adapter
        binding.rvMessageList.layoutManager = LinearLayoutManager(requireContext())


        binding.fabAddMessage.setOnClickListener {
            val action = MessageListFragmentDirections.actionMessageListFragmentToAddEditMessageFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.messagelist_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    
}
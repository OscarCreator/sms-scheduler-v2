package com.oscarcreator.sms_scheduler_v2.messages

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.SmsSchedulerApplication
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentMessagesBinding
import kotlinx.coroutines.launch

class MessagesFragment : Fragment(), ActionMode.Callback {

    private var _binding: FragmentMessagesBinding? = null

    private val binding: FragmentMessagesBinding
        get() = _binding!!

    private var actionMode: ActionMode? = null

    private lateinit var adapter: MessageAdapter

    //TODO convert to livedata
    private var selectedCount: Int = 0

    private val viewModel by viewModels<MessagesViewModel> {
        MessagesViewModelFactory((requireContext().applicationContext as SmsSchedulerApplication).messagesRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    //example https://www.androidhive.info/2017/02/android-creating-gmail-like-inbox-using-recyclerview/

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.messages_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.new_message -> {
                actionMode?.finish()
                val action =
                    MessagesFragmentDirections.actionMessageListFragmentToAddEditMessageFragment()
                findNavController().navigate(action)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)

        adapter = MessageAdapter().also {
            it.setOnMessageClickedListener(
                MessageAdapter.OnMessageClickedListener { position: Int, message: Message ->
                if (actionMode == null) {
//                    val dialog = MessageBottomSheetDialogFragment()
//                    dialog.setOnCompleteButtonClicked {
//                        findNavController().previousBackStackEntry?.savedStateHandle?.set("key",
//                            message.id)
//                        findNavController().popBackStack()
//                    }
//                    dialog.arguments = Bundle().apply { putString("message-key", message.message) }
//                    dialog.show(childFragmentManager, "bottom-sheet")
                    val action = MessagesFragmentDirections.actionMessageListFragmentToMessageDetailFragment(message.id)
                    findNavController().navigate(action)
                } else {
                    selectItem(it, position)
                    Log.d("TEST", "is selected: ${it.selectionList[position]}")
                }

            })
            it.setOnMessageLongClickedListener(
                MessageAdapter.OnMessageLongClickedListener { position: Int ->

                when (actionMode) {
                    null -> {
                        actionMode = requireActivity().startActionMode(this)
                        selectItem(it, position)

                        true
                    }
                    else -> false
                }

            })

        }

        binding.rvMessageList.layoutManager = LinearLayoutManager(activity)
        binding.rvMessageList.adapter = this.adapter

        viewModel.messages.observe(viewLifecycleOwner, {
            //show all messages
            adapter.setMessages(it)
        })

        return binding.root
    }

    private fun selectItem(adapter: MessageAdapter, position: Int){
        if (adapter.selectionList[position]) selectedCount-- else selectedCount++
        adapter.selectionList[position] = !adapter.selectionList[position]
        adapter.notifyItemChanged(position)

        actionMode?.title = "$selectedCount Selected"
    }

    //https://developer.android.com/guide/topics/ui/menus.html#CAB
    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        val inflater = mode.menuInflater
        inflater.inflate(R.menu.messages_context_fragment_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        //return false if nothing is done
        mode.title = "$selectedCount Selected"
        return false
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {

                //TODO how to make sure they are deleted before user exits fragment?
                lifecycleScope.launch {
                    val list: Array<Message> = adapter.getSelectedItems()
                    viewModel.deleteMessages(*list)
                }
                mode.finish()

                true
            }
            else -> false
        }

    }

    override fun onDestroyActionMode(mode: ActionMode) {
        actionMode = null
        selectedCount = 0
        adapter.removeSelections()
        adapter.notifyDataSetChanged()

    }


}
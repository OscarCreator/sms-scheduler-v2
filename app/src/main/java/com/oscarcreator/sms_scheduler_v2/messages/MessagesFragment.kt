package com.oscarcreator.sms_scheduler_v2.messages

import android.os.Bundle
import android.view.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.snackbar.Snackbar
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.SmsSchedulerApplication
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentMessagesBinding
import com.oscarcreator.sms_scheduler_v2.util.EventObserver

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

                        val action =
                            MessagesFragmentDirections.actionMessageListFragmentToMessageDetailFragment(
                                message.messageId
                            )
                        findNavController().navigate(action)
                    } else {
                        selectItem(it, position)
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

        binding.fabAddMessage.setOnClickListener {
            actionMode?.finish()
            val action =
                MessagesFragmentDirections.actionMessageListFragmentToAddEditMessageFragment()
            findNavController().navigate(action)
        }

        viewModel.messages.observe(viewLifecycleOwner, {
            //show all messages
            adapter.setMessages(it)
        })

        viewModel.deleteMessageEvent.observe(viewLifecycleOwner, EventObserver {
            actionMode?.finish()
        })

        viewModel.snackbarText.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(binding.root, getString(it), Snackbar.LENGTH_SHORT)
                .setAnchorView(binding.fabAddMessage)
                .show()
        })

        return binding.root
    }

    private fun selectItem(adapter: MessageAdapter, position: Int) {
        if (adapter.selectionList[position]) {
            selectedCount--
            if (selectedCount == 0) {
                actionMode?.finish()
                return
            }

        } else selectedCount++
        adapter.selectionList[position] = !adapter.selectionList[position]
        adapter.notifyItemChanged(position)

        actionMode?.title = selectedCount.toString()
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        val inflater = mode.menuInflater
        inflater.inflate(R.menu.messages_context_fragment_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        //return false if nothing is done

        mode.title = getString(R.string.selected_count, selectedCount)
        return false
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                viewModel.deleteMessages(*adapter.getSelectedItems())
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

// https://developer.android.com/jetpack/compose/tutorial

@Composable
fun MessageItem() {
    var isSelected by remember { mutableStateOf(false) }

    val surfaceColor: Color by animateColorAsState(
        if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.surface
    )
    Surface(
        modifier = Modifier.clickable { isSelected = !isSelected }.fillMaxWidth().wrapContentHeight(),
        elevation = 1.dp,
        color = surfaceColor
    ) {
        Text(
            "Some weird text",
            style = MaterialTheme.typography.body2
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMessageItem() {
    MdcTheme {
        MessageItem()
    }
}
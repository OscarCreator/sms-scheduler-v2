package com.oscarcreator.sms_scheduler_v2.addeditmessage

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentAddeditMessageBinding
import kotlinx.coroutines.launch

class AddEditMessageFragment : Fragment() {

    private var _binding: FragmentAddeditMessageBinding? = null

    private val binding: FragmentAddeditMessageBinding
        get() = _binding!!

    private lateinit var database: AppDatabase

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
                viewLifecycleOwner.lifecycleScope.launch {
                    database.messageDao().insert(
                        Message(message = binding.etMessage.text.toString(), isTemplate = binding.cbTemplate.isChecked))
                }
                //close keyboard
                binding.etMessage.clearFocus()
                findNavController().popBackStack()
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

        database = AppDatabase.getDatabase(requireContext(), lifecycleScope)


        return binding.root
    }
}
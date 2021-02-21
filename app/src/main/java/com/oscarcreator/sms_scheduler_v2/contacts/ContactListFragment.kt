package com.oscarcreator.sms_scheduler_v2.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.oscarcreator.sms_scheduler_v2.addeditscheduledtreatment.ContactsListAdapter
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentContactListBinding

class ContactListFragment : Fragment() {

    private var _binding: FragmentContactListBinding? = null
    private val binding: FragmentContactListBinding
        get() = _binding!!

    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactListBinding.inflate(inflater, container, false)
        database = AppDatabase.getDatabase(requireContext(), lifecycleScope)


        val adapter = ContactsListAdapter(ContactsListAdapter.OnContactClickedListener {
            Toast.makeText(requireContext(), "view contact ${it.name}", Toast.LENGTH_LONG).show()
        })

        database.customerDao().getCustomers().observe(viewLifecycleOwner) {
            adapter.setContacts(it)
        }

        binding.rvContactList.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.fabAddContact.setOnClickListener {
            val action = ContactListFragmentDirections.actionContactListFragmentToAddEditContactFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }
}
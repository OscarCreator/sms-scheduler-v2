package com.oscarcreator.sms_scheduler_v2.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.composethemeadapter.MdcTheme
import com.oscarcreator.sms_scheduler_v2.SmsSchedulerApplication
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentComposeViewBinding
import com.oscarcreator.sms_scheduler_v2.util.EventObserver

class ContactsFragment : Fragment() {

    private var _binding: FragmentComposeViewBinding? = null
    private val binding: FragmentComposeViewBinding
        get() = _binding!!

    private val viewModel by viewModels<ContactsViewModel> {
        ContactsViewModelFactory((requireContext().applicationContext as SmsSchedulerApplication).customersRepository)
    }


    @OptIn(ExperimentalMaterialApi::class)
    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComposeViewBinding.inflate(inflater, container, false)

        binding.composeView.setContent {
            MdcTheme {
                ContactsScreen(viewModel)
            }
        }

        viewModel.openContactEvent.observe(viewLifecycleOwner, EventObserver {
            val action = ContactsFragmentDirections
                .actionContactListFragmentToDetailContactFragment(it)
            findNavController().navigate(action)
        })

        viewModel.newContactEvent.observe(viewLifecycleOwner, EventObserver {
            val action =
                ContactsFragmentDirections.actionContactListFragmentToAddEditContactFragment()
            findNavController().navigate(action)
        })

        return binding.root
    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalMaterialApi
@Composable
fun ContactsScreen(contactsViewModel: ContactsViewModel) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { contactsViewModel.addNewContact() }) {
                Icon(Icons.Filled.Add, "Add new contact")
            }

        }
    ) {
        val contacts by contactsViewModel.contacts.observeAsState()

        contacts?.let { items ->
            ContactList(
                contacts = items,
                onClick = { contactId -> contactsViewModel.openContact(contactId) })
        }
    }


}


@OptIn(ExperimentalMaterialApi::class)
@ExperimentalMaterialApi
@Composable
fun ContactList(contacts: List<Customer>, onClick: (id: Long) -> Unit) {
    LazyColumn {
        items(contacts) { contact ->
            ListItem(
                text = {
                    Text(contact.name)
                },
                secondaryText = {
                    Text(contact.phoneNumber)
                },
                modifier = Modifier.clickable { onClick(contact.id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun PreviewContactList() {
    val contacts = listOf(
        Customer(1, "Name 1", "030405356", 300),
        Customer(1, "Name 2", "072984242", 500),
        Customer(1, "Name 3", "02384902", 1200)

    )
    ContactList(contacts = contacts, onClick = {})
}
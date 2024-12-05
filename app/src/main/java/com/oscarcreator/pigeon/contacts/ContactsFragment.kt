package com.oscarcreator.pigeon.contacts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.composethemeadapter.MdcTheme
import com.oscarcreator.pigeon.SmsSchedulerApplication
import com.oscarcreator.pigeon.data.contact.Contact
import com.oscarcreator.pigeon.databinding.FragmentComposeViewBinding
import com.oscarcreator.pigeon.util.EventObserver
import com.oscarcreator.pigeon.R

@OptIn(ExperimentalMaterialApi::class)
class ContactsFragment : Fragment() {

    private var _binding: FragmentComposeViewBinding? = null
    private val binding: FragmentComposeViewBinding
        get() = _binding!!

    private val viewModel by viewModels<ContactsViewModel> {
        ContactsViewModelFactory((requireContext().applicationContext as SmsSchedulerApplication).contactsRepository)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.contacts_fragment_menu, menu)
    }

    //callback
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                navigateToImportContacts()
            } else {
                //Explain to user that feature is unavailable because they clicked "never show again"
                //Toast.makeText(requireContext(), getString(R.string.permission_denied), Toast.LENGTH_LONG).show()

                android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Contacts Permission required")
                    .setMessage("Contacts permission is required to retrieve all your contacts")
                    .setPositiveButton("Ok") { _, _ ->
                        findNavController().navigateUp()
                    }
                    .create().show()
            }
        }

    private fun navigateToImportContacts() {
        val action = ContactsFragmentDirections.actionContactListFragmentToLoadContactsFragment()
        findNavController().navigate(action)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.import_contacts -> {

                when {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_CONTACTS
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        //permission granted
                        navigateToImportContacts()
                    }
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                        //TODO Show educational UI before requesting permission again. Alertdialog?, Snackbar?
                        //TODO replace with resource
                        android.app.AlertDialog.Builder(requireContext())
                            .setTitle("Contacts Permission required")
                            .setMessage("Contacts permission is required to retrieve all your contacts. Do you want to allow this permission?")
                            .setPositiveButton("Yes") { _, _ ->
                                requestPermissionLauncher.launch(
                                    Manifest.permission.READ_CONTACTS
                                )
                            }.setNegativeButton("No") { _, _ -> }
                            .create().show()


                    }
                    else -> {
                        // You can directly ask for the permission.
                        // The registered ActivityResultCallback gets the result of this request.
                        requestPermissionLauncher.launch(
                            Manifest.permission.READ_CONTACTS
                        )
                    }
                }
            }

            R.id.action_search -> {
                (item.actionView as SearchView).setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        viewModel.searchText.value = query
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        viewModel.searchText.value = newText
                        return false
                    }
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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

@ExperimentalMaterialApi
@Composable
fun ContactsScreen(contactsViewModel: ContactsViewModel) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { contactsViewModel.addNewContact() }
            ) {
                Icon(Icons.Filled.Add, "Add new contact")
            }

        }
    ) {

        Column {

            val searchText by contactsViewModel.searchText.observeAsState()
            val contacts by contactsViewModel.getContactsLike(searchText ?: "").observeAsState()

            contacts?.let { items ->
                ContactList(
                    contacts = items,
                    onClick = { contactId -> contactsViewModel.openContact(contactId) })
            }
        }
    }


}


@ExperimentalMaterialApi
@Composable
fun ContactList(contacts: List<Contact>, onClick: (id: Long) -> Unit) {
    LazyColumn {
        items(contacts) { contact ->
            ListItem(
                text = {
                    Text(contact.name)
                },
                secondaryText = {
                    Text(contact.phoneNumber)
                },
                modifier = Modifier.clickable { onClick(contact.contactId) }
            )
        }
    }
}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun PreviewContactList() {
    val contacts = listOf(
        Contact("Name 1", "030405356", 300),
        Contact("Name 2", "072984242", 500),
        Contact("Name 3", "02384902", 1200)

    )
    ContactList(contacts = contacts, onClick = {})
}
package com.oscarcreator.sms_scheduler_v2.importcontacts

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.navigation.fragment.findNavController
import com.google.android.material.composethemeadapter.MdcTheme
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.SmsSchedulerApplication
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentComposeViewBinding
import com.oscarcreator.sms_scheduler_v2.util.EventObserver
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
class ImportContactsFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        private const val TAG = "LoadContactsFragment"
    }

    private var _binding: FragmentComposeViewBinding? = null

    private val binding: FragmentComposeViewBinding
        get() = _binding!!

//    private lateinit var adapter: LoadContactsAdapter

    private val viewModel by viewModels<ImportContactsViewModel> {
        ImportContactsViewModelFactory((requireContext().applicationContext as SmsSchedulerApplication).contactsRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        //Init the loader
        LoaderManager.getInstance(this).initLoader(0, null, this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.load_contacts_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
//            R.id.complete -> {
//                viewModel.save()
//                return true
//            }

            R.id.action_search -> {
                (item.actionView as SearchView).setOnQueryTextListener(object :
                    SearchView.OnQueryTextListener {
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentComposeViewBinding.inflate(inflater, container, false)

        binding.composeView.setContent {
            MdcTheme {
                ImportContactsScreen(viewModel)
            }
        }

        viewModel.savedContactsEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        return binding.root
    }

    private val PROJECTION: Array<out String> = arrayOf(
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )

    //Only get the contacts with at least one number
    private val SELECTION: String =
        "${ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER} LIKE ? "

    // Defines a variable for the search string
    private val searchString: String = "1"

    // Defines the array to hold values that replace the ?
    private val selectionArgs = arrayOf(searchString)


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {

        return CursorLoader(
            requireActivity(),
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            PROJECTION,
            SELECTION,
            selectionArgs,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC "
        )

    }

    //TODO display that contact is already added. ex with a different name.
    @SuppressLint("Range")
    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {


        //TODO make it not skip frames by using the cursor in the adapter
        lifecycleScope.launch {
            val list = mutableListOf<ImportContact>()

            if (data != null) {
                if (data.moveToFirst()) {
                    do {
                        val name =
                            data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        val phoneNum =
                            data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                        list.add(ImportContact(name, phoneNum))

                    } while (data.moveToNext())
                }
            }
            Log.d(TAG, "count: ${data?.count}")
            if (viewModel.items.isEmpty()) {
                viewModel.setItems(list)
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        //cursorAdapter?.swapCursor(null)
    }

    //TODO retrieve contacts
    // https://developer.android.com/training/contacts-provider/retrieve-names#SelectionCriteria

}

data class ImportContact(
    val name: String,
    val phoneNumber: String,
    val selected: Boolean = false,
    val importedName: String? = null
)

@ExperimentalMaterialApi
@Composable
fun ImportContactsScreen(viewModel: ImportContactsViewModel) {
    val searchText by viewModel.searchText.observeAsState()
    val list = viewModel.getContactsLike(searchText ?: "")
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.save() }) {
                Icon(Icons.Filled.Check, null)
            }

        }) {
        Column {
            ListItem(
                icon = {
                    Checkbox(
                        modifier = Modifier.padding(8.dp),
                        checked = list.all { it.selected },
                        onCheckedChange = {
                            viewModel.selectAll(it)
                        }
                    )

                },
                text = {
                    Text(LocalContext.current.getString(R.string.select_all))
                }
            )
            Divider()

            ImportContactsList(list) { index ->
                viewModel.selectItem(index)
            }
        }
    }
}


@ExperimentalMaterialApi
@Composable
fun ImportContactsList(list: List<ImportContact>, onSelectItem: (index: Int) -> Unit) {
    LazyColumn {
        itemsIndexed(list) { index, item ->
            ListItem(
                modifier = Modifier.clickable(enabled = item.importedName == null) {
                    onSelectItem(index)
                },
                text = {
                    if (item.importedName != null && item.name != item.importedName) {
                        Text(
                            text = "${item.name} ${LocalContext.current.getString(R.string.as_name)} ${item.importedName}",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    } else {
                        Text(
                            text = item.name,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }

                },
                secondaryText = {
                    if (item.phoneNumber != item.name) {
                        Text(item.phoneNumber)
                    }
                },
                trailing = {
                    if (item.importedName != null) {
                        Text(LocalContext.current.getString(R.string.imported))
                    }
                },
                icon = {
                    Checkbox(
                        enabled = item.importedName == null,
                        modifier = Modifier.padding(8.dp),
                        checked = item.selected,
                        onCheckedChange = {
                            onSelectItem(index)
                        }
                    )
                }
            )
        }
    }
}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun PreviewImportContactsList() {
    val list = listOf(
        ImportContact("First Last", "+384828945"),
        ImportContact("Second Lastname", "+2389692348", selected = true, importedName = "Second")
    )
    MdcTheme {
        ImportContactsList(list = list, onSelectItem = {})
    }
}
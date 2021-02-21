package com.oscarcreator.sms_scheduler_v2.settings

import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentLoadContactsBinding
import kotlinx.coroutines.launch

class LoadContactsFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        private const val TAG = "LoadContactsFragment"
    }

    private var _binding: FragmentLoadContactsBinding? = null

    private val binding: FragmentLoadContactsBinding
        get() = _binding!!

    private lateinit var adapter: LoadContactsAdapter

    private lateinit var database: AppDatabase

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

        when (item.itemId){
            R.id.complete -> {
                requireActivity().lifecycleScope.launch {
                    val list = adapter.getSelected()
                    for (pair in list){
                        database.customerDao().insert(
                            Customer(name = pair.first, phoneNumber = pair.second))
                    }
                }
                findNavController().popBackStack()
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

        _binding = FragmentLoadContactsBinding.inflate(inflater, container, false)
        database = AppDatabase.getDatabase(requireContext(), lifecycleScope)

        adapter = LoadContactsAdapter()

        binding.rvContactSelectable.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@LoadContactsFragment.adapter

        }


        return binding.root
    }


    private val PROJECTION: Array<out String> = arrayOf(
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )

    //TODO only select the contacts with phone number
    //ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER

    //Only get the contacts with at least one number
    private val SELECTION: String =
        "${ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER} LIKE ? "

    // Defines a variable for the search string
    private val searchString: String = "1"

    // Defines the array to hold values that replace the ?
    private val selectionArgs = arrayOf(searchString)


    //for adapter
//    private val FROM_COLUMNS: Array<String> =
//        arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
//            ContactsContract.CommonDataKinds.Phone.NUMBER)
//    private val TO_IDS: IntArray = intArrayOf(android.R.id.text1)



    // An adapter that binds the result Cursor to the ListView
//    private val cursorAdapter: SimpleCursorAdapter? = null




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
    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
       // cursorAdapter?.swapCursor(data)


        //TODO make it not skip frames by using the cursor in the adapter
        lifecycleScope.launch {
            val list = mutableListOf<Pair<String, String>>()

            if (data != null){
                if (data.moveToFirst()){
                    do {
                        val name = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        val phoneNum = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                        list.add(Pair(name, phoneNum))

                    }while (data.moveToNext())
                }
            }
            Log.d(TAG, "count: ${data?.count}")
            adapter.setContacts(list)
            adapter.notifyDataSetChanged()

        }

    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        //cursorAdapter?.swapCursor(null)
    }

    //TODO retrieve contacts
    // https://developer.android.com/training/contacts-provider/retrieve-names#SelectionCriteria

}
package com.oscarcreator.sms_scheduler_v2.addeditcontact

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentAddeditContactBinding
import kotlinx.coroutines.launch

class AddEditContactFragment : Fragment() {

    private var _binding: FragmentAddeditContactBinding? = null
    private val binding: FragmentAddeditContactBinding
        get() = _binding!!

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.addedit_contact_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.complete -> {
                saveContact()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddeditContactBinding.inflate(inflater, container, false)
        database = AppDatabase.getDatabase(requireContext(), lifecycleScope)

        return binding.root
    }


    private fun validContact(): Boolean {
        return binding.tvName.text!!.isNotEmpty() &&
                binding.tvPhoneNum.text!!.isNotEmpty()
    }

    private fun saveContact() {
        if (validContact()) {
            lifecycleScope.launch {
                val name = binding.tvName.text.toString()
                val phoneNumber = binding.tvPhoneNum.text.toString()
                val money = if (binding.etMoney.text?.isNotEmpty() == true)
                    binding.etMoney.text.toString().toInt() else 0

                database.customerDao().insert(
                    Customer(
                        name = name,
                        phoneNumber = phoneNumber,
                        money = money)
                )
            }
            binding.tvName.clearFocus()
            binding.tvPhoneNum.clearFocus()
            findNavController().popBackStack()

        } else {
            Toast.makeText(requireContext(), "Missing data", Toast.LENGTH_SHORT).show()
        }
    }
}
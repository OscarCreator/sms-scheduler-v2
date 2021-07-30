package com.oscarcreator.sms_scheduler_v2.settings

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.oscarcreator.sms_scheduler_v2.R

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        private const val TAG = "SettingsFragment"
    }

    //callback
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                navigateToImportContacts()
            } else {
                //Explain to user that feature is unavailable because they clicked "never show again"
                //Toast.makeText(requireContext(), getString(R.string.permission_denied), Toast.LENGTH_LONG).show()

                AlertDialog.Builder(requireContext())
                    .setTitle("Contacts Permission required")
                    .setMessage("Contacts permission is required to retrieve all your contacts")
                    .setPositiveButton("Ok") { _, _ ->
                        findNavController().navigateUp()
                    }
                    .create().show()
            }
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val importPreference: Preference? = findPreference("import_contacts")
        importPreference?.setOnPreferenceClickListener {


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

                    AlertDialog.Builder(requireContext())
                        .setTitle("Contacts Permission required")
                        .setMessage("Contacts permission is required to retrieve all your contacts. Do you want to allow this permission?")
                        .setPositiveButton("Yes") { _, _ ->
                            requestPermissionLauncher.launch(
                                Manifest.permission.READ_CONTACTS)
                        }.setNegativeButton("No") { _, _ -> }
                        .create().show()


                }
                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                        Manifest.permission.READ_CONTACTS)
                }
            }

            return@setOnPreferenceClickListener true
        }

    }

    private fun navigateToImportContacts() {
        val action = SettingsFragmentDirections.actionSettingsFragmentToLoadContactsFragment()
        findNavController().navigate(action)
    }


}
package com.oscarcreator.sms_scheduler_v2.settings

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.SmsSchedulerApplication
import com.oscarcreator.sms_scheduler_v2.util.toTimeTemplateText
import kotlin.math.min

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        private const val TAG = "SettingsFragment"

        const val SETTINGS_SHARED_PREF = "settings_shared_pref"

        const val MESSAGE_TEMPLATE_TAG = "message_template_tag"
        const val TIME_TEMPLATE_TAG = "time_template_tag"
        const val TREATMENT_TEMPLATE_TAG = "treatment_template_tag"
        const val CURRENCY_TAG = "currency_tag"
    }

    private var messageMap: Map<Long, String> = emptyMap()
    private var timeTemplateMap: Map<Long, String> = emptyMap()
    private var treatmentMap: Map<Long, String> = emptyMap()

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
        setUpImportContacts()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setUpMessageTemplate()
        setUpTimeTemplate()
        setUpTreatmentTemplate()
        setupCurrency()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setUpImportContacts() {
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
                    //TODO replace with resource
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

    private fun setUpMessageTemplate() {
        val defaultMessage: DropDownPreference? = findPreference("default_message")

        (requireContext().applicationContext as SmsSchedulerApplication).messagesRepository.observeMessages().observe(viewLifecycleOwner) {

            val mutableMap = it.map { message -> Pair(message.messageId, message.message) }.toMutableList()
            mutableMap.add(0, Pair(-1L, getString(R.string.none)))

            messageMap = mutableMap.toMap()

            defaultMessage?.entryValues = messageMap.map {pair ->
                "${pair.key}"
            }.toTypedArray()

            defaultMessage?.entries = messageMap.map {pair ->
                "${pair.value.subSequence(0, min(pair.value.length, 40))}"
            }.toTypedArray()

            val id = requireContext().getSharedPreferences(SETTINGS_SHARED_PREF, Context.MODE_PRIVATE).getLong(
                MESSAGE_TEMPLATE_TAG, -1L)
            if (id != -1L) {
                defaultMessage?.summary = messageMap[id]
            }

        }

        defaultMessage?.setOnPreferenceChangeListener { preference, newValue ->

            val id = newValue.toString().toLong()

            requireContext().getSharedPreferences(SETTINGS_SHARED_PREF, Context.MODE_PRIVATE).edit()
                .putLong(MESSAGE_TEMPLATE_TAG, id)
                .apply()

            if (id != -1L) {
                defaultMessage.summary = messageMap[id]
            } else {
                defaultMessage.summary = ""
            }

            true
        }
    }

    private fun setUpTimeTemplate() {
        val defaultTimeTemplate: DropDownPreference? = findPreference("default_timetemplate")

        (requireContext().applicationContext as SmsSchedulerApplication).timeTemplatesRepository.observeTimeTemplates().observe(viewLifecycleOwner) {

            timeTemplateMap = it.map { timeTemplate -> Pair(timeTemplate.timeTemplateId, timeTemplate.delay.toTimeTemplateText()) }.toMutableList().let { list ->
                list.add(0, Pair(-1, getString(R.string.none)))
                list
            }.toMap()

            defaultTimeTemplate?.entryValues = timeTemplateMap.map {timeTemplate ->
                "${timeTemplate.key}"
            }.toTypedArray()

            defaultTimeTemplate?.entries = timeTemplateMap.map {timeTemplate ->
                timeTemplate.value
            }.toTypedArray()

            val id = requireContext().getSharedPreferences(SETTINGS_SHARED_PREF, Context.MODE_PRIVATE).getLong(
                TIME_TEMPLATE_TAG, -1L)
            if (id != -1L) {
                defaultTimeTemplate?.summary = timeTemplateMap[id]

            }

        }

        defaultTimeTemplate?.setOnPreferenceChangeListener { preference, newValue ->

            val id = newValue.toString().toLong()

            requireContext().getSharedPreferences(SETTINGS_SHARED_PREF, Context.MODE_PRIVATE).edit()
                .putLong(TIME_TEMPLATE_TAG, id)
                .apply()
            if (id != -1L) {
                defaultTimeTemplate.summary = timeTemplateMap[id]
            } else {
                defaultTimeTemplate.summary = ""
            }


            true
        }


    }

    private fun setUpTreatmentTemplate() {
        val defaultTreatmentTemplate: DropDownPreference? = findPreference("default_treatment")

        (requireContext().applicationContext as SmsSchedulerApplication).treatmentsRepository.observeTreatments().observe(viewLifecycleOwner) {

            treatmentMap = it.map { treatmentTemplate -> Pair(treatmentTemplate.treatmentId, treatmentTemplate.name) }.toMutableList().let { list ->
                list.add(0, Pair(-1L, getString(R.string.none)))
                list
            }.toMap()

            defaultTreatmentTemplate?.entryValues = treatmentMap.map {treatmentTemplate ->
                "${treatmentTemplate.key}"
            }.toTypedArray()

            defaultTreatmentTemplate?.entries = treatmentMap.map {treatmentTemplate ->
                treatmentTemplate.value
            }.toTypedArray()

            val id = requireContext().getSharedPreferences(SETTINGS_SHARED_PREF, Context.MODE_PRIVATE).getLong(
                TREATMENT_TEMPLATE_TAG, -1L)
            if (id != -1L) {
                defaultTreatmentTemplate?.summary = treatmentMap[id]

            }

        }

        defaultTreatmentTemplate?.setOnPreferenceChangeListener { preference, newValue ->

            val id = newValue.toString().toLong()
            requireContext().getSharedPreferences(SETTINGS_SHARED_PREF, Context.MODE_PRIVATE).edit()
                .putLong(TREATMENT_TEMPLATE_TAG, id)
                .apply()
            if (id != -1L) {
                defaultTreatmentTemplate.summary = treatmentMap[id]
            } else {
                defaultTreatmentTemplate.summary = ""
            }


            true
        }


    }

    private fun setupCurrency() {
        val currency: Preference? = findPreference("currency")

        currency?.summary = requireContext().getSharedPreferences(SETTINGS_SHARED_PREF, Context.MODE_PRIVATE).getString(
            CURRENCY_TAG, requireContext().getString(R.string.default_currency))

        currency?.setOnPreferenceClickListener {

            val currencyText = requireContext().getSharedPreferences(SETTINGS_SHARED_PREF, Context.MODE_PRIVATE).getString(
                CURRENCY_TAG, requireContext().getString(R.string.default_currency))



            val input = EditText(requireContext()).apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                hint = requireContext().getString(R.string.currency_text_hint)
                setText(currencyText)

            }

            val layout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                updatePadding(30, 30, 30, 30)
                addView(input)
            }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.change_currency))
                .setView(layout)
                .setPositiveButton(requireContext().getString(R.string.ok)) { _, _ ->
                    if (input.text.isNotEmpty()) {
                        requireContext().getSharedPreferences(SETTINGS_SHARED_PREF, Context.MODE_PRIVATE).edit()
                            .putString(CURRENCY_TAG, input.text.toString())
                            .apply()

                        currency.summary = requireContext().getSharedPreferences(SETTINGS_SHARED_PREF, Context.MODE_PRIVATE).getString(
                            CURRENCY_TAG, requireContext().getString(R.string.default_currency))
                    }
                }.setNeutralButton(requireContext().getString(R.string.cancel)) { _, _ -> }
                .show()

            true
        }


    }

    private fun navigateToImportContacts() {
        val action = SettingsFragmentDirections.actionSettingsFragmentToLoadContactsFragment()
        findNavController().navigate(action)
    }


}
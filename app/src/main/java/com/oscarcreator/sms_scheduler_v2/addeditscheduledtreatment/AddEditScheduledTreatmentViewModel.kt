package com.oscarcreator.sms_scheduler_v2.addeditscheduledtreatment

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oscarcreator.sms_scheduler_v2.data.Result
import com.oscarcreator.sms_scheduler_v2.data.contact.Contact
import com.oscarcreator.sms_scheduler_v2.data.contact.ContactsRepository
import com.oscarcreator.sms_scheduler_v2.data.message.MessagesRepository
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentContactCrossRef
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageAndTimeTemplateAndContacts
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplatesRepository
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import com.oscarcreator.sms_scheduler_v2.data.treatment.TreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.settings.SettingsFragment
import com.oscarcreator.sms_scheduler_v2.util.Event
import com.oscarcreator.sms_scheduler_v2.util.scheduleAlarm
import com.oscarcreator.sms_scheduler_v2.util.toTimeTemplateText
import kotlinx.coroutines.launch
import java.util.*

class AddEditScheduledTreatmentViewModel(
    private val contactsRepository: ContactsRepository,
    private val treatmentsRepository: TreatmentsRepository,
    private val timeTemplatesRepository: TimeTemplatesRepository,
    private val messagesRepository: MessagesRepository,
    private val scheduledTreatmentsRepository: ScheduledTreatmentsRepository
) : ViewModel() {

    companion object {
        private const val TAG = "AddEditTreatmentViewModel"
    }

    val message = MutableLiveData<String>()

    val timeTemplateText = MutableLiveData<String>()

    private var _contacts: MutableList<Contact> = mutableListOf()
    val contacts: List<Contact>
        get() = _contacts

    private val _customersLoadedEvent = MutableLiveData<Event<Unit>>()
    val customersLoadedEvent: LiveData<Event<Unit>> = _customersLoadedEvent

    private val _allTreatments = treatmentsRepository.observeTreatments()
    val allTreatment = _allTreatments

    private val _scheduledTreatmentUpdatedEvent = MutableLiveData<Event<Unit>>()
    val scheduledTreatmentUpdatedEvent: LiveData<Event<Unit>> = _scheduledTreatmentUpdatedEvent

    private var _messageId = -1L
    private var _timeTemplateId = -1L

    val time = MutableLiveData<Long>()
    val treatment = MutableLiveData<Treatment>()

    private var scheduledTreatmentId: Long? = 1
    private var isNewScheduledTreatment = false
    private var isDataLoaded = false

    suspend fun getCustomersLike(text: String): List<Contact> {
        return contactsRepository.getCustomersLike(text)
    }

    suspend fun setMessageById(messageId: Long) {
        viewModelScope.launch {
            messagesRepository.getMessage(messageId).let { result ->
                if (result is Result.Success) {
                    _messageId = messageId
                    message.value = result.data.message
                } else {
                    _messageId = -1L
                    message.value = ""
                }
            }
        }
    }

    suspend fun setTimeTemplateById(timeTemplateId: Long) {
        viewModelScope.launch {
            timeTemplatesRepository.getTimeTemplate(timeTemplateId).let { result ->
                if (result is Result.Success) {
                    _timeTemplateId = timeTemplateId
                    timeTemplateText.value = result.data.delay.toTimeTemplateText()
                } else {
                    _timeTemplateId = -1L
                    timeTemplateText.value = ""
                }
            }
        }
    }

    suspend fun setTreatmentById(id: Long) {
        treatmentsRepository.getTreatment(id).let {
            if (it is Result.Success) {
                onTreatmentLoaded(it.data)
            } else {
                //onDataNotAvailable()
            }
        }
    }

    private fun onTreatmentLoaded(treatment: Treatment) {
        this.treatment.value = treatment
    }

    fun addReceiver(receiver: Contact) {
        _contacts.add(receiver)
    }

    fun removeReceiver(index: Int) {
        _contacts.removeAt(index)
    }

    fun removeReceiver(contact: Contact){
        _contacts.remove(contact)
    }

    fun start(scheduledTreatmentId: Long? = null, context: Context) {
        if (scheduledTreatmentId == null) {
            isNewScheduledTreatment = true

            val messageId = context.getSharedPreferences(SettingsFragment.SETTINGS_SHARED_PREF, Context.MODE_PRIVATE)
                .getLong(SettingsFragment.MESSAGE_TEMPLATE_TAG, -1L)
            if (messageId != -1L) {
                viewModelScope.launch {
                    setMessageById(messageId)
                }
            }

            val timeTemplateId = context.getSharedPreferences(SettingsFragment.SETTINGS_SHARED_PREF, Context.MODE_PRIVATE)
                .getLong(SettingsFragment.TIME_TEMPLATE_TAG, -1L)
            if (timeTemplateId != -1L) {
                viewModelScope.launch {
                    setTimeTemplateById(timeTemplateId)
                }
            }

            val treatmentId = context.getSharedPreferences(SettingsFragment.SETTINGS_SHARED_PREF, Context.MODE_PRIVATE)
                .getLong(SettingsFragment.TREATMENT_TEMPLATE_TAG, -1L)
            if (treatmentId != -1L) {
                viewModelScope.launch {
                    setTreatmentById(treatmentId)
                }
            }

            return
        }
        if (this.scheduledTreatmentId == scheduledTreatmentId && !isNewScheduledTreatment && isDataLoaded) {
            //Already initialized
            return
        }
        this.scheduledTreatmentId = scheduledTreatmentId
        isNewScheduledTreatment = false

        viewModelScope.launch {
            scheduledTreatmentsRepository.getScheduledTreatmentWithData(scheduledTreatmentId).let {
                if (it != null){
                    onScheduledTreatmentLoaded(it)
                }else{
                    //TODO idk what do do here
                    // show a toast?, crash the app?
                    Log.wtf(TAG, "Passed scheduletreatmentid does not exist.")
                }
            }
        }

    }

    private fun onScheduledTreatmentLoaded(
        scheduledTreatmentWithData: ScheduledTreatmentWithMessageAndTimeTemplateAndContacts
    ) {
        time.value = scheduledTreatmentWithData.scheduledTreatment.treatmentTime.timeInMillis
        treatment.value = scheduledTreatmentWithData.treatment
        timeTemplateText.value = scheduledTreatmentWithData.timeTemplate.delay.toTimeTemplateText()
        message.value = scheduledTreatmentWithData.message.message

        _contacts = scheduledTreatmentWithData.contacts.toMutableList()

        isDataLoaded = true
        _customersLoadedEvent.value = Event(Unit)
    }

    fun saveScheduledTreatment(context: Context) {
        val currentReceivers = _contacts
        val currentTime = time.value
        val currentTreatment = treatment.value
        val currentTimeTemplate = timeTemplateText.value
        val currentMessage = message.value
        //Why not?
        Log.d(TAG, "receivers: ${currentReceivers.isEmpty()}")
        Log.d(TAG, "currenttime: ${currentTime != null} && ${currentTime != 0L}")
        Log.d(TAG, "treatment: ${currentTreatment != null}")
        Log.d(TAG, "timeTemplate: ${currentTimeTemplate != null}")
        if (currentReceivers.isNotEmpty()
            && currentTime != null && currentTime != 0L
            && currentTreatment != null
            && currentTimeTemplate != null && currentTimeTemplate.isNotEmpty() && _timeTemplateId != -1L
            && currentMessage != null && currentMessage.isNotEmpty() && _messageId != -1L
        ) {
            if (isNewScheduledTreatment || scheduledTreatmentId == null) {
                createScheduledTreatment(context,
                    ScheduledTreatment(
                        treatmentId = currentTreatment.treatmentId,
                        treatmentTime = Calendar.getInstance(Locale.getDefault())
                            .apply { timeInMillis = currentTime },
                        timeTemplateId = _timeTemplateId,
                        messageId = _messageId
                    ),
                    currentReceivers
                )
            } else {
                updateScheduledTreatment(context,
                    ScheduledTreatment(
                        id = scheduledTreatmentId!!,
                        treatmentId = currentTreatment.treatmentId,
                        treatmentTime = Calendar.getInstance(Locale.getDefault())
                            .apply { timeInMillis = currentTime },
                        timeTemplateId = _timeTemplateId,
                        messageId = _messageId
                    ),
                    currentReceivers
                )
            }

        } else {
            //notify with a text that data is missing
            return
        }

    }


    /**
     * Call this when about to create a new scheduledTreatment
     * */
    private fun createScheduledTreatment(
        context: Context,
        newScheduledTreatment: ScheduledTreatment,
        receivers: List<Contact>
    ) =
        viewModelScope.launch {
            // launch is used to run it in a coroutine for communicating with database through repository
            val newId = scheduledTreatmentsRepository.insert(newScheduledTreatment)

            createScheduledTreatmentCustomerCrossRef(newId, receivers)
            scheduleAlarm(context, scheduledTreatmentsRepository.getScheduledTreatmentWithData(newId)!!)

            _scheduledTreatmentUpdatedEvent.value = Event(Unit)
        }

    private fun updateScheduledTreatment(
        context: Context,
        scheduledTreatment: ScheduledTreatment,
        receivers: List<Contact>
    ) {
        if (isNewScheduledTreatment) {
            throw RuntimeException("update() was callled but scheduledTreatment is new")
        }
        viewModelScope.launch {
            scheduledTreatmentsRepository.update(scheduledTreatment)
            updateScheduledTreatmentCustomerCrossRef(scheduledTreatment.id, receivers)

            scheduleAlarm(context, scheduledTreatmentsRepository.getScheduledTreatmentWithData(scheduledTreatment.id)!!)
            _scheduledTreatmentUpdatedEvent.value = Event(Unit)
        }
    }

    private suspend fun createScheduledTreatmentCustomerCrossRef(scheduledTreatmentId: Long, receivers: List<Contact>){
        for (receiver in receivers) {
            scheduledTreatmentsRepository.insertCrossRef(
                ScheduledTreatmentContactCrossRef(scheduledTreatmentId, receiver.contactId))
        }
    }

    private suspend fun updateScheduledTreatmentCustomerCrossRef(
        scheduledTreatmentId: Long,
        receivers: List<Contact>
    ) {
        // Delete all old receivers
        scheduledTreatmentsRepository.deleteCrossRefs(scheduledTreatmentId)
        // Insert all new recievers
        createScheduledTreatmentCustomerCrossRef(scheduledTreatmentId, receivers)
    }

}


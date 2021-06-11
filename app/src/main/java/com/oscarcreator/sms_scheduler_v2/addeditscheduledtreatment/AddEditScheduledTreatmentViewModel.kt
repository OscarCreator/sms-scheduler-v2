package com.oscarcreator.sms_scheduler_v2.addeditscheduledtreatment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer
import com.oscarcreator.sms_scheduler_v2.data.customer.CustomersRepository
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.message.MessagesRepository
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentCustomerCrossRef
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplatesRepository
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import com.oscarcreator.sms_scheduler_v2.data.treatment.TreatmentsRepository
import kotlinx.coroutines.launch
import java.util.*

class AddEditScheduledTreatmentViewModel(
    private val customersRepository: CustomersRepository,
    private val treatmentsRepository: TreatmentsRepository,
    private val timeTemplatesRepository: TimeTemplatesRepository,
    private val messagesRepository: MessagesRepository,
    private val scheduledTreatmentsRepository: ScheduledTreatmentsRepository
) : ViewModel() {

    companion object {
        private const val TAG = "AddEditTreatmentViewModel"
    }

    private val _customers = MutableLiveData<MutableList<Customer>>(mutableListOf())
    //TODO change to list so they can't be changed
    val customers: LiveData<MutableList<Customer>> = _customers

    val time = MutableLiveData<Long>()
    val treatment = MutableLiveData<Treatment>()
    val timeModifier = MutableLiveData<TimeTemplate>()
    val message = MutableLiveData<Message>()

    private val _allTreatments = treatmentsRepository.getTreatments()
    val allTreatment = _allTreatments

    // Further implementation
    // https://github.com/android/architecture-samples/blob/main/app/src/main/java/com/example/android/architecture/blueprints/todoapp/addedittask/AddEditTaskViewModel.kt

    private var scheduledTreatmentId: Long? = 1
    private var isNewScheduledTreatment = false
    private var isDataLoaded = false

    suspend fun getCustomersLike(text: String): List<Customer> {
        return customersRepository.getCustomersLike(text)
    }

    suspend fun setMessageById(id: Long) {
        message.value = messagesRepository.getMessage(id)
    }

    suspend fun setTimeTemplateById(id: Long) {
        timeModifier.value = timeTemplatesRepository.getTimeTemplate(id)
    }


    fun addReceiver(receiver: Customer) {
        _customers.value!!.add(receiver)
    }

    fun removeReceiver(index: Int) {
        _customers.value!!.removeAt(index)
    }

    fun removeReceiver(customer: Customer){
        _customers.value!!.remove(customer)
    }

    fun start(scheduledTreatmentId: Long? = null) {
        if (scheduledTreatmentId == null) {
            isNewScheduledTreatment = true
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
        scheduledTreatmentWithData: ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers
    ) {
        time.value = scheduledTreatmentWithData.scheduledTreatment.treatmentTime.timeInMillis
        treatment.value = scheduledTreatmentWithData.treatment
        timeModifier.value = scheduledTreatmentWithData.timeTemplate
        message.value = scheduledTreatmentWithData.message

        _customers.value = scheduledTreatmentWithData.customers.toMutableList()

        isDataLoaded = true
        Log.d("HELLO", "loaded values!!!")
    }

    fun saveScheduledTreatment() {
        val currentReceivers = _customers.value
        val currentTime = time.value
        val currentTreatment = treatment.value
        val currentTimeTemplate = timeModifier.value
        val currentMessage = message.value
        if (currentReceivers != null && currentReceivers.isEmpty()
            && currentTime != null && currentTime != 0L
            && currentTreatment != null
            && currentTimeTemplate != null
            && currentMessage != null
        ) {
            if (isNewScheduledTreatment || scheduledTreatmentId == null) {
                createScheduledTreatment(
                    ScheduledTreatment(
                        treatmentId = currentTreatment.id,
                        treatmentTime = Calendar.getInstance(Locale.getDefault())
                            .apply { timeInMillis = currentTime },
                        timeTemplateId = currentTimeTemplate.id,
                        messageId = currentMessage.id
                    ),
                    currentReceivers
                )
            } else {
                updateScheduledTreatment(
                    ScheduledTreatment(
                        id = scheduledTreatmentId!!,
                        treatmentId = currentTreatment.id,
                        treatmentTime = Calendar.getInstance(Locale.getDefault())
                            .apply { timeInMillis = currentTime },
                        timeTemplateId = currentTimeTemplate.id,
                        messageId = currentMessage.id
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
        newScheduledTreatment: ScheduledTreatment,
        receivers: List<Customer>
    ) =
        viewModelScope.launch {
            // launch is used to run it in a coroutine for communicating with database through repository
            val newId = scheduledTreatmentsRepository.insert(newScheduledTreatment)

            createScheduledTreatmentCustomerCrossRef(newId, receivers)
        }

    private fun updateScheduledTreatment(
        scheduledTreatment: ScheduledTreatment,
        receivers: List<Customer>
    ) {
        if (isNewScheduledTreatment) {
            throw RuntimeException("update() was callled but scheduledTreatment is new")
        }
        viewModelScope.launch {
            scheduledTreatmentsRepository.update(scheduledTreatment)
            updateScheduledTreatmentCustomerCrossRef(scheduledTreatment.id, receivers)
        }
    }

    private suspend fun createScheduledTreatmentCustomerCrossRef(scheduledTreatmentId: Long, receivers: List<Customer>){
        for (receiver in receivers) {
            scheduledTreatmentsRepository.insertCrossRef(
                ScheduledTreatmentCustomerCrossRef(scheduledTreatmentId, receiver.id))
        }
    }

    private suspend fun updateScheduledTreatmentCustomerCrossRef(
        scheduledTreatmentId: Long,
        receivers: List<Customer>
    ) {
        // Delete all old receivers
        scheduledTreatmentsRepository.deleteCrossRefs(scheduledTreatmentId)
        // Insert all new recievers
        createScheduledTreatmentCustomerCrossRef(scheduledTreatmentId, receivers)
    }

}


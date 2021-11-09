package com.oscarcreator.sms_scheduler_v2.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.SmsSchedulerApplication
import com.oscarcreator.sms_scheduler_v2.data.contact.Contact
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageTimeTemplateAndContact
import com.oscarcreator.sms_scheduler_v2.data.scheduled.SmsStatus
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentComposeViewBinding
import com.oscarcreator.sms_scheduler_v2.util.dateToText
import com.oscarcreator.sms_scheduler_v2.util.sendSmsNow
import java.util.*

class NotificationsFragment : Fragment() {

    private var _binding: FragmentComposeViewBinding? = null
    private val binding: FragmentComposeViewBinding
        get() = _binding!!

    private val viewModel by viewModels<NotificationsViewModel> {
        NotificationsViewModelFactory(
            (requireContext().applicationContext as SmsSchedulerApplication).scheduledTreatmentsRepository
        )
    }


    @ExperimentalMaterialApi
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComposeViewBinding.inflate(inflater, container, false)

        binding.composeView.setContent {
            MdcTheme {
                NotificationCards(viewModel, { scheduledTreatment ->
                    retrySendSms(scheduledTreatment)
                }, {
                    viewScheduledTreatment(it)
                }, {
                    dismiss(it)
                }
                )
            }
        }

        return binding.root
    }

    private fun retrySendSms(scheduledTreatment: ScheduledTreatmentWithMessageTimeTemplateAndContact) {
        sendSmsNow(requireContext(), scheduledTreatment)
    }

    private fun viewScheduledTreatment(id: Long) {
        findNavController().navigate(R.id.scheduledTreatmentDetailFragment, Bundle().apply { putLong("scheduledTreatmentId", id) })
    }


    private fun dismiss(id: Long) {
        val items = arrayOf(getString(R.string.sent), getString(R.string.done))
        var checkedItem = 0
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.mark_treatment_as))
            .setNeutralButton(getString(R.string.cancel)) { dialog, which ->

            }
            .setPositiveButton(getString(R.string.mark_and_close)) { dialog, which ->
                when (checkedItem) {
                    0 -> viewModel.markScheduledTreatmentAsSent(id)
                    1 -> viewModel.markScheduledTreatmentAsDone(id)
                }
            }
            .setSingleChoiceItems(items, checkedItem) { dialog, which ->
                checkedItem = which
            }
            .show()
    }

}

@ExperimentalMaterialApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationCards(
    viewModel: NotificationsViewModel,
    onRetrySendSms: (scheduledTreatment: ScheduledTreatmentWithMessageTimeTemplateAndContact) -> Unit = {},
    onViewScheduledTreatment: (id: Long) -> Unit = {},
    onDismiss: (id: Long) -> Unit = {}
) {

    val scheduledTreatments by viewModel.failedScheduleTreatmentsWithData.observeAsState()
    LazyRow(content = {
        scheduledTreatments?.let {
            items(it) { scheduledTreatment ->
                NotificationCard(scheduledTreatment = scheduledTreatment, onRetrySendSms, onViewScheduledTreatment, onDismiss)

            }

        }
    })

}

@ExperimentalMaterialApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationCard(
    scheduledTreatment: ScheduledTreatmentWithMessageTimeTemplateAndContact,
    onRetrySendSms: (scheduledTreatment: ScheduledTreatmentWithMessageTimeTemplateAndContact) -> Unit = {},
    onViewScheduledTreatment: (id: Long) -> Unit = {},
    onDismiss: (id: Long) -> Unit = {}
) {

    Card(onClick = { /*TODO*/ }, modifier = Modifier) {
        Column(modifier = Modifier.padding(8.dp)) {
            Column(modifier = Modifier.padding(8.dp)) {
                // title
                Text(
                    stringResource(R.string.sms_not_sent),
                    style = MaterialTheme.typography.h6
                )

                val calendarSendTime by remember { mutableStateOf(
                    Calendar.getInstance().apply {
                        timeInMillis = scheduledTreatment.getSendTime()
                    })
                }

                val calendar by remember {
                    mutableStateOf(Calendar.getInstance())
                }

                val sendTimeText = calendarSendTime.dateToText(LocalContext.current, calendar)
                // supporting text
                Text(
                    stringResource(R.string.sms_failed_known, scheduledTreatment.contact.name, sendTimeText),
                    modifier = Modifier.padding(top = 16.dp),
                    color = MaterialTheme.colors.secondary,
                    style = MaterialTheme.typography.body2,
                    softWrap = true,

                )
            }

            Row(modifier = Modifier.padding(8.dp)) {
                if (scheduledTreatment.scheduledTreatment.smsStatus == SmsStatus.ERROR) {

                    Button(
                        {
                            onRetrySendSms(scheduledTreatment)
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {

                        Text(stringResource(R.string.retry_send))
                    }
                } else {

                    Button(
                        {
                            onViewScheduledTreatment(scheduledTreatment.scheduledTreatment.scheduledTreatmentId)
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {

                        Text(stringResource(R.string.view))
                    }

                }

                Button(
                    {
                        onDismiss(scheduledTreatment.scheduledTreatment.scheduledTreatmentId)
                    },
                ) {
                    Text(stringResource(R.string.dismiss))
                    // mark treatmentstatus : canceled

                }
            }
        }
    }


}

@ExperimentalMaterialApi
@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun PreviewNotificationCard() {
    val scheduledTreatment = ScheduledTreatmentWithMessageTimeTemplateAndContact(
        scheduledTreatment = ScheduledTreatment(
            1,
            1,
            1,
            1,
            Calendar.getInstance(),
            smsStatus = SmsStatus.ERROR
        ),
        message = Message("Some weird message"),
        timeTemplate = TimeTemplate(1000 * 60 * 60 * 14),
        treatment = Treatment("Treatement 1", 500, 60),
        contact = Contact("Bengt Bengtsson", "0482394246")
    )

    NotificationCard(scheduledTreatment)
}


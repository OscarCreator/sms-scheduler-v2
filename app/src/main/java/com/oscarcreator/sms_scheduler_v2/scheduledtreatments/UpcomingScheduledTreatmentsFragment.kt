package com.oscarcreator.sms_scheduler_v2.scheduledtreatments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.composethemeadapter.MdcTheme
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.data.contact.Contact
import com.oscarcreator.sms_scheduler_v2.data.message.Message
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatment
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageTimeTemplateAndContact
import com.oscarcreator.sms_scheduler_v2.data.scheduled.SmsStatus
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentScheduledtreatmentsUpcomingBinding
import com.oscarcreator.sms_scheduler_v2.util.dateToText
import java.util.*

class UpcomingScheduledTreatmentsFragment : Fragment() {

    private var _binding: FragmentScheduledtreatmentsUpcomingBinding? = null
    private val binding
        get() = _binding!!


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduledtreatmentsUpcomingBinding.inflate(inflater, container, false)

        val adapter = ScheduledTreatmentAdapter()

        adapter.setOnScheduledTreatmentClickedListener(
                ScheduledTreatmentAdapter.OnScheduledTreatmentClickedListener { position, scheduledTreatment ->

                    val action = ScheduledTreatmentsFragmentDirections
                            .actionScheduledTreatmentsFragmentToScheduledTreatmentDetailFragment(
                                    scheduledTreatment.scheduledTreatment.id)
                    findNavController().navigate(action)

                })

        // TODO extract to viewmodel
        val database = AppDatabase.getDatabase(requireContext(), lifecycleScope)
        //which upcoming should it choose? upcoming treatments or sms
        database.scheduledTreatmentDao()
                .getUpcomingScheduledTreatmentsWithData(Calendar.getInstance(Locale.getDefault()))
                .observe(viewLifecycleOwner) {

                    adapter.setScheduledTreatments(it)
                    adapter.notifyDataSetChanged()
                }


        binding.recyclerView.apply {
            this.adapter = adapter
            this.layoutManager = LinearLayoutManager(requireContext())
        }

        return binding.root
    }

}

@Composable
fun ScheduledTreatmentCard(scheduledTreatment: ScheduledTreatmentWithMessageTimeTemplateAndContact, onClick: () -> Unit = {}) {

    val calendarSendTime by remember { mutableStateOf(
        Calendar.getInstance().apply {
            timeInMillis = scheduledTreatment.getSendTime()
        })
    }

    val calendar by remember {
        mutableStateOf(Calendar.getInstance())
    }

    val sendTimeText = calendarSendTime.dateToText(LocalContext.current, calendar)


    Column(modifier = Modifier
        .clickable(onClick = onClick)
        .padding(all = 8.dp)) {

        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {

            val (name, status) = createRefs()

            createHorizontalChain(name, status, chainStyle = ChainStyle.SpreadInside)

            Text(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .constrainAs(name) {
                            start.linkTo(parent.start)
                            end.linkTo(name.start)
                            centerVerticallyTo(parent)
                            width = Dimension.fillToConstraints
                        },
                    text = scheduledTreatment.contact.name,
                    style = MaterialTheme.typography.h6,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
            )

            Row(modifier = Modifier.constrainAs(status) {
                end.linkTo(parent.end)
                centerVerticallyTo(parent)
            }) {
                Icon(
                        painter =
                        when (scheduledTreatment.scheduledTreatment.smsStatus) {
                            SmsStatus.SENT -> {
                                painterResource(R.drawable.ic_check)
                            }
                            SmsStatus.RECEIVED -> {
                                painterResource(R.drawable.ic_done_all)
                            }
                            else -> {
                                painterResource(R.drawable.ic_schedule_send)
                            }

                        },
                        contentDescription = null,
                        modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.CenterVertically)
                )
                if (scheduledTreatment.scheduledTreatment.smsStatus == SmsStatus.SCHEDULED) {

                    //TODO side effect? convert to remember {}

                    Text(
                            text = sendTimeText,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }

        Row {
            Text(
                    text = scheduledTreatment.treatment.name,
                    style = MaterialTheme.typography.caption
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                    text = scheduledTreatment.scheduledTreatment.treatmentTime.dateToText(LocalContext.current, calendar),
                    style = MaterialTheme.typography.caption
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
                text = scheduledTreatment.message.message,
                style = MaterialTheme.typography.body2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScheduledTreatmentCard() {
    MdcTheme {
        ScheduledTreatmentCard(
                ScheduledTreatmentWithMessageTimeTemplateAndContact(
                        ScheduledTreatment(0, 1, Calendar.getInstance(), 1, 1, 1),
                        Message("Hello, welcome to treatment tomorrow 5/4 12:00 at our location. If you want to unbook then you can do that until 24h before your treatment.", messageId = 1),
                        TimeTemplate( 1000L * 60L * 60L),
                        Treatment("Treatmentpi", 100, 40),
                        Contact( "Anders Andersson, Bengt bengtsson", "070205302", 600)
                )
        )
    }
}
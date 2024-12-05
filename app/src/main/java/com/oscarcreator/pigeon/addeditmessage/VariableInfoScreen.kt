package com.oscarcreator.pigeon.addeditmessage

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.android.material.composethemeadapter.MdcTheme
import com.oscarcreator.pigeon.data.scheduled.ScheduledTreatmentWithMessageTimeTemplateAndContact
import java.text.SimpleDateFormat
import java.util.*
import com.oscarcreator.pigeon.R

val VARIABLE_CALENDAR_VALUES = listOf(Calendar.MINUTE, Calendar.HOUR_OF_DAY, Calendar.DATE, Calendar.MONTH, Calendar.YEAR)

val VARIABLE_VALUES = listOf("[name]", "[min]", "[hour]", "[day]", "[month]", "[year]", "[service]", "[date1]", "[time1]")

@Composable
fun VariableInfoScreen() {

    val variableDescriptionArray = stringArrayResource(R.array.message_variable_descriptions)

    Column {
        VARIABLE_VALUES.forEachIndexed { index, s ->
            VariableInfoItem(s, variableDescriptionArray[index])
        }
    }
}

@Composable
fun VariableInfoItem(title: String, description: String) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(Modifier.clickable {
        isExpanded = !isExpanded

    }) {

        Surface(
            elevation = 1.dp,
        ) {

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                val (titleRef, iconRef) = createRefs()

                createHorizontalChain(titleRef, iconRef, chainStyle = ChainStyle.SpreadInside)

                Text(
                    text = title,
                    modifier = Modifier
                        .constrainAs(titleRef) {
                            start.linkTo(parent.start)
                            end.linkTo(iconRef.start)
                            centerVerticallyTo(parent)
                            width = Dimension.fillToConstraints

                        },
                )
                Icon(
                    painter = if (isExpanded) painterResource(R.drawable.ic_arrow_up) else painterResource(
                        R.drawable.ic_arrow_down
                    ),
                    modifier = Modifier.constrainAs(iconRef) {
                        end.linkTo(parent.end)
                        start.linkTo(titleRef.end)
                        centerVerticallyTo(parent)

                    },
                    contentDescription = null
                )
            }
        }

        Surface(
            modifier = Modifier.animateContentSize()
        ) {
            if (isExpanded) {
                Text(
                    text = description,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun PreviewVariableInfoScreen() {
    MdcTheme() {
        VariableInfoScreen()
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewSystemUiVariableInfoScreen() {
    MdcTheme() {
        VariableInfoScreen()
    }
}

fun ScheduledTreatmentWithMessageTimeTemplateAndContact.replaceVariables(): String {
    var text = message.message

    val date1Format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val time1Format = SimpleDateFormat("HH:mm", Locale.getDefault())

    VARIABLE_VALUES.forEachIndexed { index, s ->
        text = when {
            index == 0 -> {
                text.replace(s, contact.name)
            }
            s == "[month]" -> {
                //month value start with 0 - 11 and not 1 - 12
                text.replace(s, scheduledTreatment.treatmentTime.get(VARIABLE_CALENDAR_VALUES[index - 1]).plus(1).toString())
            }
            s == "[min]" -> {
                val minutes = scheduledTreatment.treatmentTime.get(VARIABLE_CALENDAR_VALUES[index - 1]).toString()
                when {
                    minutes == "0" -> {
                        text.replace(s, "${minutes}0")
                    }
                    minutes.length == 1 -> {
                        text.replace(s, "0${minutes}")
                    }
                    else -> {
                        text.replace(s, minutes)
                    }
                }
            }
            s == "[service]" -> {
                text.replace(s, treatment.name)
            }

            s == "[date1]" -> {
                text.replace(s, date1Format.format(scheduledTreatment.treatmentTime.time))
            }

            s == "[time1]" -> {
                text.replace(s, time1Format.format(scheduledTreatment.treatmentTime.time))
            }
            else -> {
                text.replace(s, scheduledTreatment.treatmentTime.get(VARIABLE_CALENDAR_VALUES[index - 1]).toString())
            }
        }
    }
    return text
}
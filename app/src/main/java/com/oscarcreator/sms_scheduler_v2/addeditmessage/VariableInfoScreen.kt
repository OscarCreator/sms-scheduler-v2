package com.oscarcreator.sms_scheduler_v2.addeditmessage

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
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers
import java.util.*

val VARIABLE_CALENDAR_VALUES = listOf(Calendar.MINUTE, Calendar.HOUR_OF_DAY, Calendar.DATE, Calendar.MONTH, Calendar.YEAR)

val VARIABLE_VALUES = listOf("[name]", "[min]", "[hour]", "[day]", "[month]", "[year]")

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

fun ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers.replaceVariables(): String {
    var text = message.message

    VARIABLE_VALUES.forEachIndexed { index, s ->
        text = when {
            index == 0 -> {
                text.replace(s, customers[0].name)
            }
            s == "[month]" -> {
                //month value start with 0 - 11 and not 1 - 12
                text.replace(s, scheduledTreatment.treatmentTime.get(VARIABLE_CALENDAR_VALUES[index - 1]).plus(1).toString())
            }
            else -> {
                text.replace(s, scheduledTreatment.treatmentTime.get(VARIABLE_CALENDAR_VALUES[index - 1]).toString())
            }
        }
    }
    return text
}
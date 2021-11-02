package com.oscarcreator.sms_scheduler_v2.treatments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.composethemeadapter.MdcTheme
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.SmsSchedulerApplication
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentComposeViewBinding
import com.oscarcreator.sms_scheduler_v2.util.EventObserver

class TreatmentsFragment : Fragment() {

    private var _binding: FragmentComposeViewBinding? = null

    private val binding: FragmentComposeViewBinding
        get() = _binding!!

    private val viewModel by viewModels<TreatmentsViewModel> {
        TreatmentsViewModelFactory((requireContext().applicationContext as SmsSchedulerApplication).treatmentsRepository)

    }
    @OptIn(ExperimentalMaterialApi::class)
    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComposeViewBinding.inflate(inflater, container, false)

        binding.composeView.setContent {
            MdcTheme {
                TreatmentsScreen(viewModel)
            }
        }

        viewModel.openTreatmentEvent.observe(viewLifecycleOwner, EventObserver {
            val action = TreatmentsFragmentDirections.actionTreatmentsFragmentToTreatmentDetailFragment(it)
            findNavController().navigate(action)
        })

        viewModel.newTreatmentEvent.observe(viewLifecycleOwner, EventObserver {
            val action = TreatmentsFragmentDirections.actionTreatmentsFragmentToAddEditTreatment()
            findNavController().navigate(action)
        })

        return binding.root
    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalMaterialApi
@Composable
fun TreatmentsScreen(treatmentsViewModel: TreatmentsViewModel) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { treatmentsViewModel.addNewTreatment() }) {
                Icon(Icons.Filled.Add, "Add new treatment")
            }
        }
    ) {
        val treatments by treatmentsViewModel.treatments.observeAsState()

        treatments?.let { items ->
            TreatmentList(items) { treatmentsViewModel.openTreatment(it) }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalMaterialApi
@Composable
fun TreatmentList(treatments: List<Treatment>, onClick: (treatmentId: Long) -> Unit) {
    LazyColumn {
        items(treatments) { treatment ->
            ListItem(
                text = {
                    Text(treatment.name)
                },
                secondaryText = {
                    Text(LocalContext.current
                        .getString(R.string.treatment_price, treatment.price))

                },
                trailing = {
                    Text(LocalContext.current
                        .getString(R.string.treatment_time, treatment.duration))
                },
                modifier = Modifier.clickable { onClick(treatment.treatmentId) }
            )
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun PreviewTreatmentList() {
    val treatments = listOf(
        Treatment("Treatment 1", 500, 50),
        Treatment("Treatment 2", 400, 40),
        Treatment("Treatment 3", 1900, 110)
    )
    MdcTheme {
        TreatmentList(treatments = treatments, onClick = {})
    }
}
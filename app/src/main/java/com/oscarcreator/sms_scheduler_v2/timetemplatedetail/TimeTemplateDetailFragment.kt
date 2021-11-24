package com.oscarcreator.sms_scheduler_v2.timetemplatedetail

import android.os.Bundle
import android.view.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.composethemeadapter.MdcTheme
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.SmsSchedulerApplication
import com.oscarcreator.sms_scheduler_v2.util.EventObserver
import com.oscarcreator.sms_scheduler_v2.util.toTimeTemplateText

class TimeTemplateDetailFragment : Fragment() {

    private val args: TimeTemplateDetailFragmentArgs by navArgs()

    private val viewModel by viewModels<TimeTemplateDetailViewModel> {
        TimeTemplateDetailViewModelFactory((requireContext().applicationContext as SmsSchedulerApplication).timeTemplatesRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.timetemplate_detail_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit -> {
                viewModel.editTimeTemplate()
                return true
            }

            R.id.delete -> {
                viewModel.deleteTimeTemplate()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.start(args.timetemplateId)

        viewModel.editTimeTemplateEvent.observe(viewLifecycleOwner, EventObserver {
            val action = TimeTemplateDetailFragmentDirections
                .actionTimeTemplateDetailFragmentToAddEditTimeTemplateFragment(args.timetemplateId)
            findNavController().navigate(action)
        })


        viewModel.deleteTimeTemplateEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MdcTheme {
                    TimeTemplateDetailScreen()
                }
            }
        }
    }

    @Composable
    fun TimeTemplateDetailScreen() {
        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text(getString(R.string.choose).uppercase(), style = MaterialTheme.typography.button) },
                    onClick = {
                        findNavController().popBackStack(R.id.addEditScheduledTreatmentFragment, false)
                        findNavController().getBackStackEntry(R.id.addEditScheduledTreatmentFragment).savedStateHandle.set("timetemplate-id-key", args.timetemplateId)
                    },
                    icon = { Icon(Icons.Filled.Check, null) },
                )
            },
            floatingActionButtonPosition = FabPosition.Center,

            ) {
            val timeTemplate by viewModel.timeTemplate.observeAsState()

            timeTemplate?.delay?.toTimeTemplateText()?.let {
                Text(it)
            }
        }
    }

    @Preview()
    @Composable
    fun PreviewTimeTemplateDetailScreen() {
        MdcTheme {
            TimeTemplateDetailScreen()
        }
    }


}
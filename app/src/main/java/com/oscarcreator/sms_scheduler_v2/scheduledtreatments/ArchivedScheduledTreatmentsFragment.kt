package com.oscarcreator.sms_scheduler_v2.scheduledtreatments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.composethemeadapter.MdcTheme
import com.oscarcreator.sms_scheduler_v2.data.AppDatabase
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentComposeViewBinding
import java.util.*

class ArchivedScheduledTreatmentsFragment: Fragment() {

    private var _binding: FragmentComposeViewBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComposeViewBinding.inflate(inflater, container, false)

        val database = AppDatabase.getDatabase(requireContext(), lifecycleScope)

        binding.composeView.setContent {
            MdcTheme {
                ArchivedScheduledTreatmentsScreen(database) {
                    val action = ScheduledTreatmentsFragmentDirections
                        .actionScheduledTreatmentsFragmentToScheduledTreatmentDetailFragment(it)
                    findNavController().navigate(action)
                }
            }
        }

        return binding.root
    }
}


// TODO change to viewmodel instead of database
@Composable
fun ArchivedScheduledTreatmentsScreen(database: AppDatabase, onClick: (scheduledTreatmentId: Long) -> Unit) {
    val currentTime by remember { mutableStateOf(Calendar.getInstance()) }
    val scheduledTreatments by database.scheduledTreatmentDao().getOldScheduledTreatmentsWithData(currentTime).observeAsState()
    LazyColumn {
        scheduledTreatments?.let {
            items(it) { scheduledTreatment ->
                ScheduledTreatmentCard(scheduledTreatment) {
                    onClick(scheduledTreatment.scheduledTreatment.scheduledTreatmentId)
                }
            }
        }
    }
}
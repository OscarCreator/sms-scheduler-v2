package com.oscarcreator.pigeon.timetemplates

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.oscarcreator.pigeon.R
import com.oscarcreator.pigeon.SmsSchedulerApplication
import com.oscarcreator.pigeon.data.timetemplate.TimeTemplate
import com.oscarcreator.pigeon.databinding.FragmentTimetemplateListBinding
import com.oscarcreator.pigeon.util.EventObserver

class TimeTemplatesFragment : Fragment(), ActionMode.Callback {

    private var _binding: FragmentTimetemplateListBinding? = null

    private val binding: FragmentTimetemplateListBinding
        get() = _binding!!

    private var actionMode: ActionMode? = null

    private lateinit var adapter: TimeTemplateAdapter

    private val viewModel by viewModels<TimeTemplatesViewModel> {
        TimeTemplatesViewModelFactory((requireContext().applicationContext as SmsSchedulerApplication).timeTemplatesRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTimetemplateListBinding.inflate(inflater, container, false)

        adapter = TimeTemplateAdapter().also {
            it.setOnTimeTemplateClickedListener(
                TimeTemplateAdapter.OnTimeTemplateClickedListener { position: Int, timeTemplate: TimeTemplate ->
                    if (actionMode == null) {
                        val action = TimeTemplatesFragmentDirections
                            .actionTimeTemplateListFragmentToTimeTemplateDetailFragment(timeTemplate.timeTemplateId)
                        findNavController().navigate(action)
                    } else {
                        selectItem(it, position)
                    }
                })
            it.setOnTimeTemplateLongClickedListener(
                TimeTemplateAdapter.OnTimeTemplateLongClickedListener {position: Int ->
                    when (actionMode) {
                        null -> {
                            actionMode = requireActivity().startActionMode(this)
                            selectItem(it, position)
                            true
                        }
                        else -> false
                    }
                })
        }

        binding.rvTimetemplateList.apply {
            adapter = this@TimeTemplatesFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.fabAddTimetemplate.setOnClickListener {
            val action = TimeTemplatesFragmentDirections.actionTimeTemplateListFragmentToAddEditTimeTemplateFragment()
            findNavController().navigate(action)
            actionMode?.finish()
        }

        viewModel.timeTemplates.observe(viewLifecycleOwner) {
            adapter.setTimeTemplates(it)
            if (it.isEmpty()) {
                binding.timeTemplateTutorial.visibility = View.VISIBLE
            } else {
                binding.timeTemplateTutorial.visibility = View.GONE
            }
        }

        viewModel.deleteMessageEvent.observe(viewLifecycleOwner, EventObserver {
            actionMode?.finish()
        })

        viewModel.snackbarText.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(binding.root, getString(it), Snackbar.LENGTH_SHORT)
                .setAnchorView(binding.fabAddTimetemplate)
                .show()
        })

        return binding.root
    }


    private fun selectItem(adapter: TimeTemplateAdapter, position: Int) {
        if (adapter.selectionList[position]) {
            viewModel.selectedCount--
            if (viewModel.selectedCount == 0) {
                actionMode?.finish()
                return
            }
        } else viewModel.selectedCount++

        adapter.selectionList[position] = !adapter.selectionList[position]
        adapter.notifyItemChanged(position)

        actionMode?.title = getString(R.string.selected_count, viewModel.selectedCount)
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(R.menu.timetemplates_action_fragment_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        //return false if nothing is done
        actionMode?.title = getString(R.string.selected_count, viewModel.selectedCount)
        return false
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                viewModel.deleteTimeTemplates(*adapter.getSelectedItems())
                true
            }
            else -> false
        }
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        actionMode = null
        viewModel.selectedCount = 0
        adapter.removeSelections()
        adapter.notifyDataSetChanged()
    }


}
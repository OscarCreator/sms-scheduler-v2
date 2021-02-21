package com.oscarcreator.sms_scheduler_v2.timetemplates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentDialogTimetemplateBinding
import com.oscarcreator.sms_scheduler_v2.util.toTimeTemplateText

class TimeTemplateBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var listener: OnCompleteButtonClicked? = null

    private var _binding: FragmentDialogTimetemplateBinding? = null

    private val binding: FragmentDialogTimetemplateBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogTimetemplateBinding.inflate(inflater, container, false)


        val millis = requireArguments().getLong("timetemplate-key")

        binding.tvTimetemplate.text = millis.toTimeTemplateText()

        binding.btnUse.setOnClickListener {
            listener?.onCompleteButtonClicked()
        }

        return binding.root
    }


    fun setOnCompleteButtonClicked(listener: () -> Unit){
        this.listener = object : OnCompleteButtonClicked {
            override fun onCompleteButtonClicked() {
                listener()
            }
        }
    }

    interface OnCompleteButtonClicked {
        fun onCompleteButtonClicked()
    }

}

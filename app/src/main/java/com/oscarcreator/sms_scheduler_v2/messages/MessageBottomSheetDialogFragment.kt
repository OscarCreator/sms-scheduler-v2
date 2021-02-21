package com.oscarcreator.sms_scheduler_v2.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oscarcreator.sms_scheduler_v2.databinding.FragmentDialogMessageBinding

class MessageBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var listener: OnCompleteButtonClicked? = null

    private var _binding: FragmentDialogMessageBinding? = null

    private val binding: FragmentDialogMessageBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentDialogMessageBinding.inflate(inflater, container, false)


        binding.tvMessage.text = arguments?.getString("message-key")

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
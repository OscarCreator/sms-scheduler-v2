package com.oscarcreator.sms_scheduler_v2.timetemplatelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplate
import com.oscarcreator.sms_scheduler_v2.util.toTimeTemplateText

class TimeTemplateAdapter(private val listener: OnTimeTemplateClickedListener? = null) : RecyclerView.Adapter<TimeTemplateAdapter.TimeTemplateViewHolder>() {

    private var list: List<TimeTemplate> = emptyList()

    inner class TimeTemplateViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val tvTimeTemplate: MaterialTextView = itemView.findViewById(R.id.tv_timetemplate)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener?.onTimeTemplateClicked(list[adapterPosition])
        }

    }

    interface OnTimeTemplateClickedListener {
        fun onTimeTemplateClicked(timeTemplate: TimeTemplate)

        companion object {
            inline operator fun invoke(crossinline function: (TimeTemplate) -> Unit) =
                object : OnTimeTemplateClickedListener {
                    override fun onTimeTemplateClicked(timeTemplate: TimeTemplate) = function(timeTemplate)
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeTemplateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.timetemplate_list_item, parent, false)
        return TimeTemplateViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TimeTemplateViewHolder, position: Int) {
        holder.tvTimeTemplate.text = list[position].delay.toTimeTemplateText()
    }

    override fun getItemCount() = list.size

    fun setTimeTemplates(list: List<TimeTemplate>){
        this.list = list
    }

}
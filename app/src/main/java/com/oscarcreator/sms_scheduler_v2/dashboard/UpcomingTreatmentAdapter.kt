package com.oscarcreator.sms_scheduler_v2.dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers

class UpcomingTreatmentAdapter() : RecyclerView.Adapter<UpcomingTreatmentAdapter.UpcomingTreatmentViewHolder>() {

    var list: List<ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers> = emptyList()

    class UpcomingTreatmentViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val tvLabel: MaterialTextView = itemView.findViewById(R.id.tv_label)
        val tvName: MaterialTextView = itemView.findViewById(R.id.tv_name)
        val tvTime: MaterialTextView = itemView.findViewById(R.id.tv_time)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingTreatmentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.upcoming_treatment_list_item, parent, false)

        return UpcomingTreatmentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UpcomingTreatmentViewHolder, position: Int) {

        if (position >= list.size){
            //TODO remove
            // temp data
            holder.tvLabel.text = "Delivered"
            holder.tvName.text = "Sid Ronny"
            holder.tvTime.text = "14:00"
        }else{
            val current = list[position]
            holder.tvLabel.text = current.scheduledTreatment.label.name
            //TODO change to allow more than one customer
            holder.tvName.text = current.customers[0].name
            holder.tvTime.text = current.scheduledTreatment.treatmentTime.timeInMillis.toString()
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setScheduledTreatments(list: List<ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers>){
        this.list = list
        notifyDataSetChanged()
        Log.d("UpcomingTreatmentAdapter", "list size=${list.size}")
    }

}
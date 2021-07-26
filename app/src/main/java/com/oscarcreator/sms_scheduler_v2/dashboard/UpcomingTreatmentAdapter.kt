package com.oscarcreator.sms_scheduler_v2.dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers
import com.oscarcreator.sms_scheduler_v2.util.dateToText
import java.util.*

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
        val current = list[position]
        holder.tvLabel.text = current.scheduledTreatment.treatmentStatus.name
        holder.tvName.text = current.customers.stream().map{it.name}.toArray().joinToString(", ")
        val c = Calendar.getInstance()
        c.timeInMillis = current.scheduledTreatment.treatmentTime.timeInMillis + current.timeTemplate.delay
        //TODO use this text as a title for grouping the scheduled treatments
        // the time of sending the scheduled treatment should only be displayed per scheduled treatment
        holder.tvTime.text = c.dateToText(holder.itemView.context, Calendar.getInstance())

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
package com.oscarcreator.sms_scheduler_v2.treatments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment

class TreatmentsAdapter(val listener: OnTreatmentClickedListener? = null) : RecyclerView.Adapter<TreatmentsAdapter.TreatmentsViewHolder>() {

    private var list: List<Treatment> = emptyList()

    inner class TreatmentsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val tvTreatmentName: MaterialTextView = itemView.findViewById(R.id.tv_treatment_name)
        val tvDuration: MaterialTextView = itemView.findViewById(R.id.tv_duration)
        val tvPrice: MaterialTextView = itemView.findViewById(R.id.tv_price)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener?.onTreatmentClicked(list[adapterPosition])
        }

    }

    interface OnTreatmentClickedListener {
        fun onTreatmentClicked(treatment: Treatment)

        companion object {
            inline operator fun invoke(crossinline function: (Treatment) -> Unit) =
                object : OnTreatmentClickedListener {
                    override fun onTreatmentClicked(treatment: Treatment) = function(treatment)
                }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreatmentsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.treatment_list_item, parent, false)
        return TreatmentsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TreatmentsViewHolder, position: Int) {
        holder.tvTreatmentName.text = list[position].name
        holder.tvDuration.text = "${list[position].duration}min"
        holder.tvPrice.text = "${list[position].price}kr"
    }

    override fun getItemCount() = list.size

    fun setTreatments(list: List<Treatment>){
        this.list = list
    }
}
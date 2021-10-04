package com.oscarcreator.sms_scheduler_v2.scheduledtreatments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.composethemeadapter.MdcTheme
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.scheduled.ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers

class ScheduledTreatmentAdapter :
    RecyclerView.Adapter<ScheduledTreatmentAdapter.ScheduledTreatmentListViewHolder>() {

    private var list: List<ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers> = listOf()

    private var onScheduledTreatmentClickedListener: OnScheduledTreatmentClickedListener? = null

    init {
        setHasStableIds(true)
    }

    inner class ScheduledTreatmentListViewHolder(
        itemView: View,
        private val onScheduledTreatmentClickedListener: OnScheduledTreatmentClickedListener?
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        init {
            itemView.setOnClickListener(this)
            //itemId = list[adapterPosition].scheduledTreatment.id
        }

        override fun onClick(v: View?) {
            onScheduledTreatmentClickedListener?.onScheduledTreatmentClicked(adapterPosition, list[adapterPosition])
        }

        val composeView: ComposeView = itemView.findViewById(R.id.cv)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ScheduledTreatmentListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.upcoming_treatment_list_item, parent, false)

        return ScheduledTreatmentListViewHolder(view, onScheduledTreatmentClickedListener)
    }

    override fun onBindViewHolder(holder: ScheduledTreatmentListViewHolder, position: Int) {

        holder.composeView.setContent {
            MdcTheme() {
                ScheduledTreatmentCard(
                        scheduledTreatment = list[position],
                        onClick = {holder.itemView.callOnClick()}
                )
            }
        }
        /*
        holder.tvLabel.text = list[position].scheduledTreatment.smsStatus.name
        holder.tvName.text= list[position].customers[0].name

        val c = Calendar.getInstance()
        c.timeInMillis = list[position].getSendTime()

        val simpleTimeFormat =  SimpleDateFormat("HH:mm dd/MM-yy", Locale.getDefault())

        holder.tvTime.text = simpleTimeFormat.format(c.time)
*/
    }

    override fun getItemCount(): Int = list.size

    override fun getItemId(position: Int): Long = list[position].scheduledTreatment.id

    fun setScheduledTreatments(scheduledTreatments: List<ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers>) {
        list = scheduledTreatments
    }

    fun setOnScheduledTreatmentClickedListener(scheduledTreatmentClickedListener: OnScheduledTreatmentClickedListener) {
        this.onScheduledTreatmentClickedListener = scheduledTreatmentClickedListener
    }


    interface OnScheduledTreatmentClickedListener {
        fun onScheduledTreatmentClicked(position: Int, scheduledTreatment: ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers)

        companion object {
            inline operator fun invoke(crossinline function: (Int, ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers) -> Unit) =
                object : OnScheduledTreatmentClickedListener {
                    override fun onScheduledTreatmentClicked(position: Int, scheduledTreatment: ScheduledTreatmentWithMessageAndTimeTemplateAndCustomers) = function(position, scheduledTreatment)
                }
        }
    }

}
package com.oscarcreator.pigeon.timetemplates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.oscarcreator.pigeon.R
import com.oscarcreator.pigeon.data.timetemplate.TimeTemplate
import com.oscarcreator.pigeon.util.toTimeTemplateText

class TimeTemplateAdapter :
    RecyclerView.Adapter<TimeTemplateAdapter.TimeTemplateViewHolder>() {

    private var onTimeTemplateClickedListener: OnTimeTemplateClickedListener? = null
    private var onTimeTemplateLongClickedListener: OnTimeTemplateLongClickedListener? = null

    var selectionList: MutableList<Boolean> = mutableListOf()

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TimeTemplate>() {
        override fun areItemsTheSame(oldItem: TimeTemplate, newItem: TimeTemplate): Boolean {
            return oldItem.timeTemplateId == newItem.timeTemplateId
        }

        override fun areContentsTheSame(oldItem: TimeTemplate, newItem: TimeTemplate): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = differ.currentList[position].timeTemplateId

    inner class TimeTemplateViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        val tvTimeTemplate: MaterialTextView = itemView.findViewById(R.id.tv_timetemplate)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            onTimeTemplateClickedListener?.onTimeTemplateClicked(adapterPosition, differ.currentList[adapterPosition])
        }

        override fun onLongClick(p0: View?): Boolean {
            return onTimeTemplateLongClickedListener?.onTimeTemplateLongClicked(adapterPosition) ?: false
        }

    }

    fun setOnTimeTemplateClickedListener(onTimeTemplateClickedListener: OnTimeTemplateClickedListener) {
        this.onTimeTemplateClickedListener = onTimeTemplateClickedListener
    }

    fun setOnTimeTemplateLongClickedListener(onTimeTemplateLongClickedListener: OnTimeTemplateLongClickedListener) {
        this.onTimeTemplateLongClickedListener = onTimeTemplateLongClickedListener
    }

    interface OnTimeTemplateClickedListener {
        fun onTimeTemplateClicked(position: Int, timeTemplate: TimeTemplate)

        companion object {
            inline operator fun invoke(crossinline function: (Int, TimeTemplate) -> Unit) =
                object : OnTimeTemplateClickedListener {
                    override fun onTimeTemplateClicked(position: Int, timeTemplate: TimeTemplate) = function(position, timeTemplate)
                }
        }
    }

    interface OnTimeTemplateLongClickedListener {
        fun onTimeTemplateLongClicked(position: Int): Boolean

        companion object {
            inline operator fun invoke(crossinline function: (Int) -> Boolean) =
                object : OnTimeTemplateLongClickedListener {
                    override fun onTimeTemplateLongClicked(position: Int) = function(position)
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeTemplateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.timetemplate_list_item, parent, false)
        return TimeTemplateViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TimeTemplateViewHolder, position: Int) {
        holder.tvTimeTemplate.text = differ.currentList[position].delay.toTimeTemplateText()
        holder.itemView.isSelected = selectionList[position]
    }

    override fun getItemCount() = differ.currentList.size

    fun setTimeTemplates(list: List<TimeTemplate>){
        differ.submitList(list) {
            selectionList = MutableList(list.size) {false}
        }
    }

    fun removeSelections() {
        selectionList.replaceAll {false}
    }

    fun getSelectedItems(): Array<TimeTemplate> {
        return differ.currentList
            .filterIndexed { index, _ -> selectionList[index] }
            .toTypedArray()
    }

}
package com.oscarcreator.sms_scheduler_v2.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textview.MaterialTextView
import com.oscarcreator.sms_scheduler_v2.R

class LoadContactsAdapter : RecyclerView.Adapter<LoadContactsAdapter.LoadContactsViewHolder>() {

    private var list: List<Pair<String, String>> = emptyList()

    private var selected: MutableList<Boolean> = mutableListOf()

    inner class LoadContactsViewHolder(val itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvName: MaterialTextView = itemView.findViewById(R.id.tv_name)
        val checkbox: MaterialCheckBox = itemView.findViewById(R.id.checkbox)

        init {
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                selected[adapterPosition] = isChecked
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadContactsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.contact_selectable_item, parent, false)
        return LoadContactsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LoadContactsViewHolder, position: Int) {
        holder.tvName.text = list[position].first
        holder.checkbox.isChecked = selected[position]
        //holder.tvPhoneNumber.text = list[position].second
    }

    override fun getItemCount() = list.size

    fun setContacts(list: List<Pair<String, String>>) {
        this.list = list
        this.selected = MutableList(list.size) { false }
    }

    fun getSelected(): List<Pair<String, String>> {
        val selectedItems = mutableListOf<Pair<String, String>>()
        for ((index, pair) in list.withIndex()){
            if (selected[index]){
                selectedItems.add(pair)
            }
        }
        return selectedItems
    }


}
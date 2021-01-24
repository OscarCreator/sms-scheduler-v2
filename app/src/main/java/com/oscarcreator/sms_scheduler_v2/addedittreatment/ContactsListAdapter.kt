package com.oscarcreator.sms_scheduler_v2.addedittreatment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.oscarcreator.sms_scheduler_v2.R

class ContactsListAdapter(private val onContactClickedListener: OnContactClickedListener? = null)
    : RecyclerView.Adapter<ContactsListAdapter.ContactsListViewHolder>(){

    var list: List<Pair<String, String>> = emptyList()

    class ContactsListViewHolder(
        itemView: View,
        private val listener: OnContactClickedListener?
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val tvName: MaterialTextView = itemView.findViewById(R.id.tv_name)
        val tvPhoneNum: MaterialTextView = itemView.findViewById(R.id.tv_phone_num)


        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener?.onEntryClicked(tvName.text.toString())
        }

    }

    interface OnContactClickedListener {
        fun onEntryClicked(name: String)

        companion object {
            inline operator fun invoke(crossinline function: (String) -> Unit) =
                object : OnContactClickedListener {
                    override fun onEntryClicked(name: String) = function(name)
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.contact_list_item, parent, false)
        return ContactsListViewHolder(itemView, onContactClickedListener)
    }

    override fun onBindViewHolder(holder: ContactsListViewHolder, position: Int) {
        holder.tvName.text = list[position].first
        holder.tvPhoneNum.text = list[position].second
    }

    override fun getItemCount(): Int = list.size

    fun setContactList(list: List<Pair<String, String>>){
        this.list = list
        notifyDataSetChanged()
    }
}
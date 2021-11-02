package com.oscarcreator.sms_scheduler_v2.addeditscheduledtreatment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.contact.Contact

class ContactsListAdapter(private val onContactClickedListener: OnContactClickedListener? = null)
    : RecyclerView.Adapter<ContactsListAdapter.ContactsListViewHolder>(){

    var list: List<Contact> = emptyList()

    inner class ContactsListViewHolder(
        itemView: View,
        private val listener: OnContactClickedListener?
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val tvName: MaterialTextView = itemView.findViewById(R.id.tv_name)
        val tvPhoneNum: MaterialTextView = itemView.findViewById(R.id.tv_phone_num)


        init {
            itemView.setOnClickListener(this)

        }

        override fun onClick(v: View?) {
            listener?.onEntryClicked(list[adapterPosition])
        }

    }

    interface OnContactClickedListener {
        fun onEntryClicked(contact: Contact)

        companion object {
            inline operator fun invoke(crossinline function: (Contact) -> Unit) =
                object : OnContactClickedListener {
                    override fun onEntryClicked(contact: Contact) = function(contact)
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.contact_list_item, parent, false)
        return ContactsListViewHolder(itemView, onContactClickedListener)
    }

    override fun onBindViewHolder(holder: ContactsListViewHolder, position: Int) {
        holder.tvName.text = list[position].name
        holder.tvPhoneNum.text = list[position].phoneNumber
    }

    override fun getItemCount(): Int = list.size

    fun setContacts(list: List<Contact>){
        this.list = list
        notifyDataSetChanged()
    }
}
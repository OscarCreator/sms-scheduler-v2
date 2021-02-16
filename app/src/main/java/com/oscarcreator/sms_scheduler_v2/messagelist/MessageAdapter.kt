package com.oscarcreator.sms_scheduler_v2.messagelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.message.Message

class MessageAdapter(private val onMessageClickedListener: OnMessageClickedListener? = null) : RecyclerView.Adapter<MessageAdapter.MessageListViewHolder>() {

    private var list: List<Message> = emptyList()

    inner class MessageListViewHolder(
        itemView: View,
        private val listener: OnMessageClickedListener?
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val tvMessage: MaterialTextView = itemView.findViewById(R.id.tv_message)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener?.onMessageClicked(list[adapterPosition])
        }

    }

    interface OnMessageClickedListener {
        fun onMessageClicked(message: Message)

        companion object {
            inline operator fun invoke(crossinline function: (Message) -> Unit) =
                object : OnMessageClickedListener {
                    override fun onMessageClicked(message: Message) = function(message)
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.message_list_item, parent, false)
        return MessageListViewHolder(itemView, onMessageClickedListener)
    }

    override fun onBindViewHolder(holder: MessageListViewHolder, position: Int) {
        holder.tvMessage.text = list[position].message
    }

    override fun getItemCount(): Int = list.size

    fun setMessages(newList: List<Message>){
        this.list = newList
    }

}
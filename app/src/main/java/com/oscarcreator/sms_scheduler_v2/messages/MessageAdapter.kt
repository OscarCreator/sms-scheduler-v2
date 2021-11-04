package com.oscarcreator.sms_scheduler_v2.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.oscarcreator.sms_scheduler_v2.R
import com.oscarcreator.sms_scheduler_v2.data.message.Message

class MessageAdapter() :
    RecyclerView.Adapter<MessageAdapter.MessageListViewHolder>() {

    private var onMessageClickedListener: OnMessageClickedListener? = null
    private var onMessageLongClickedListener: OnMessageLongClickedListener? = null

    var selectionList: MutableList<Boolean> = mutableListOf()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.messageId == newItem.messageId
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int) = differ.currentList[position].messageId

    fun setOnMessageClickedListener(onMessageClickedListener: OnMessageClickedListener){
        this.onMessageClickedListener = onMessageClickedListener
    }

    fun setOnMessageLongClickedListener(onMessageLongClickedListener: OnMessageLongClickedListener){
        this.onMessageLongClickedListener = onMessageLongClickedListener
    }

    inner class MessageListViewHolder(
        itemView: View,
        private val onClickedListener: OnMessageClickedListener?,
        private val onLongClickedListener: OnMessageLongClickedListener?
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        val tvMessage: MaterialTextView = itemView.findViewById(R.id.tv_message)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View) {
            onClickedListener?.onMessageClicked(adapterPosition, differ.currentList[adapterPosition])
        }

        override fun onLongClick(v: View): Boolean {
            return onLongClickedListener?.onMessageLongClicked(adapterPosition) ?: false
        }

    }

    interface OnMessageClickedListener {
        fun onMessageClicked(position: Int, message: Message)

        companion object {
            inline operator fun invoke(crossinline function: (Int, Message) -> Unit) =
                object : OnMessageClickedListener {
                    override fun onMessageClicked(position: Int, message: Message) = function(position, message)
                }
        }
    }

    interface OnMessageLongClickedListener {
        fun onMessageLongClicked(position: Int): Boolean

        companion object {
            inline operator fun invoke(crossinline function: (Int) -> Boolean) =
                object : OnMessageLongClickedListener {
                    override fun onMessageLongClicked(position: Int) = function(position)
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.message_list_item, parent, false)
        return MessageListViewHolder(itemView, onMessageClickedListener, onMessageLongClickedListener)
    }

    override fun onBindViewHolder(holder: MessageListViewHolder, position: Int) {
        holder.tvMessage.text = differ.currentList[position].message
        holder.itemView.isSelected = selectionList[position]
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setMessages(newList: List<Message>){

        differ.submitList(newList) {
            selectionList = MutableList(newList.size) { false }
        }
    }

    fun removeSelections() {
        selectionList.replaceAll { false }
    }

    fun getSelectedItems(): Array<Message> {
        return differ.currentList
            .filterIndexed { index, _ -> selectionList[index] }
            .toTypedArray()
    }

}
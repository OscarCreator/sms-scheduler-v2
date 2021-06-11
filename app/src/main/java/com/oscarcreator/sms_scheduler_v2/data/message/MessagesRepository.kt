package com.oscarcreator.sms_scheduler_v2.data.message

import androidx.lifecycle.LiveData

interface MessagesRepository {

    fun observeMessages(): LiveData<List<Message>>

    fun observeMessage(messageId: Long): LiveData<Message>

    suspend fun getMessage(messageId: Long): Message

    suspend fun insert(message: Message): Long

    suspend fun update(message: Message): Int

    suspend fun delete(vararg messages: Message): Int

}
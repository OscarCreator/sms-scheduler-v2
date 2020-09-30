package com.oscarcreator.sms_scheduler_v2.data.message

class MessageRepository private constructor(val messageDao: MessageDao){

    fun getMessages() = messageDao.getMessages()

    suspend fun insert(message: Message) = messageDao.insert(message)

}
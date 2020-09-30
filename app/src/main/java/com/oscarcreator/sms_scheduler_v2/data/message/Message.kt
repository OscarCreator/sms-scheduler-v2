package com.oscarcreator.sms_scheduler_v2.data.message

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val message: String,
    val isTemplate: Boolean = false
)
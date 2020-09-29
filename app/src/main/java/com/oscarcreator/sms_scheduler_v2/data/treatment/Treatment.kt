package com.oscarcreator.sms_scheduler_v2.data.treatment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "treatments")
data class Treatment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val price: Int,
    /** Duration in minutes */
    val duration: Int
)
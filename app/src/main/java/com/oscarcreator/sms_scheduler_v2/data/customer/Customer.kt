package com.oscarcreator.sms_scheduler_v2.data.customer

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Customers")
data class Customer (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    val money: Int = 0
)
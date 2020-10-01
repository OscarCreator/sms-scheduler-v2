package com.oscarcreator.sms_scheduler_v2.data.customer

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "customer_id") val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    val money: Int = 0
)
package com.oscarcreator.sms_scheduler_v2.data.treatment

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A [Treatment] keeps track of the price and name of a [Treatment]
 * */
@Entity(tableName = "treatments")
data class Treatment(
    /**
     * The id of the [Treatment]. SQLite will generate an id of not specified
     * */
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    /**
     * The name of the [Treatment]
     * */
    val name: String,

    /**
     * The price of the [Treatment]
     * */
    val price: Int,

    /**
     * The duration of the [Treatment] in minutes
     * */
    val duration: Int
)
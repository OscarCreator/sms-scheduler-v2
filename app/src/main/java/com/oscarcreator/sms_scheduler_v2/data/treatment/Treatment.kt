package com.oscarcreator.sms_scheduler_v2.data.treatment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * A [Treatment] keeps track of the price and name of a Treatment
 * */
@Entity(tableName = "treatments")
data class Treatment(
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
    val duration: Int,

    /**
     * If the [Treatment] should be deleted and is only left in database because there is references to it.
     * */
    @ColumnInfo(name = "to_be_deleted") val toBeDeleted: Boolean = false,

    /**
     * The id of the [Treatment]. SQLite will generate an id of not specified
     * */
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "treatment_id") val treatmentId: Long = 0,

    /**
     * The version of the [Treatment]
     * */
    @ColumnInfo(name = "treatment_version") val treatmentVersion: Long = 0,

    /**
     * Id of the group of [Treatment]s. If a treatment is edited the new treatment is saved in the same group as the old one.
     * */
    @ColumnInfo(name = "treatment_group_id", index = true) val treatmentGroupId: String = UUID.randomUUID().toString(),

    /**
     * Time which the treatment is created.
     * */
    @ColumnInfo(name = "treatment_created") val treatmentCreated: Calendar = Calendar.getInstance()
)
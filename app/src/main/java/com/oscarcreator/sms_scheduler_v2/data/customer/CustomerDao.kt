package com.oscarcreator.sms_scheduler_v2.data.customer

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * A Data access object (Dao) for [Customer] object.
 * */
@Dao
interface CustomerDao {

    /**
     * Returns all [Customer] in a [LiveData] object.
     *
     * @return all customers
     * */
    @Query("SELECT * FROM Customers")
    fun getCustomers(): LiveData<List<Customer>>

    /**
     * Returns a the [Customer] with the id
     *
     * @param customerId the id of the [Customer] which will be returned
     * @return the [Customer] with the passed id
     * */
    @Query("SELECT * FROM Customers WHERE customer_id = :customerId")
    fun getCustomer(customerId: Long): Customer

    /**
     * Inserts a [Customer] into the database and returns the id of the inserted [Customer].
     * If a [Customer] already exist's with the passed id then the insert will be ignored.
     *
     * @param customer the [Customer] which will be inserted
     * @return the id of the inserted [Customer]
     * */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(customer: Customer): Long

    /**
     * Deletes the passed [Customer]s and returns the count of [Customer]s deleted.
     *
     * @throws SQLiteConstraintException when [Customer] is references in a scheduled treatment
     * @param customer the [Customer]s which will be deleted
     *
     * @return the count of [Customer]s deleted
     * */
    @Delete
    suspend fun delete(vararg customer: Customer): Int

    /**
     * Updates the passed [Customer] and returns the count of [Customer]s updated.
     *
     * @param customer the [Customer] which will be updated
     * @return the count of [Customer]s inserted (0 if not updated, 1 if updated)
     * */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(customer: Customer): Int

}
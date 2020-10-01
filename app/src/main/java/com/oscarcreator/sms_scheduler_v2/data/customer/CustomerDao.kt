package com.oscarcreator.sms_scheduler_v2.data.customer

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CustomerDao {

    /**
     * Returns all customers in a [LiveData] object.
     *
     * @return all customers
     * */
    @Query("SELECT * FROM Customers")
    fun getCustomers(): LiveData<List<Customer>>

    @Query("SELECT * FROM Customers WHERE customer_id = :customerId")
    fun getCustomer(customerId: Long): Customer

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(customer: Customer): Long

    /**
     * @throws SQLiteConstraintException when [Customer] is references in a scheduled treatment
     * */
    @Delete
    suspend fun delete(vararg customer: Customer): Int

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(customer: Customer): Int

}
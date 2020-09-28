package com.oscarcreator.sms_scheduler_v2.data.customer

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

    @Query("SELECT * FROM Customers WHERE id = :customerId")
    fun getCustomer(customerId: Long): Customer

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(customer: Customer)

    @Delete
    suspend fun delete(vararg customer: Customer)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(customer: Customer)

}
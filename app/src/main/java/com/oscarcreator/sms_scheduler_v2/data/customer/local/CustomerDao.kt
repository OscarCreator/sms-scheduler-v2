package com.oscarcreator.sms_scheduler_v2.data.customer.local

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.room.*
import com.oscarcreator.sms_scheduler_v2.data.customer.Customer

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
    suspend fun getCustomer(customerId: Long): Customer

    /**
     * Observes the [Customer] with the specified id
     *
     * @param id the id of the [Customer]
     * @return the customer with the specified id
     * */
    @Query("SELECT * FROM customers WHERE customer_id = :id")
    fun observeCustomer(id: Long): LiveData<Customer>

    /**
     * Returns the first five customers which is matched by name or
     *  phone number with the passed text.
     *
     *  @param text the text which is used to search through customers
     *  @return the matched customers
     * */
    @Query("SELECT * FROM customers WHERE name LIKE :text OR phone_number LIKE :text ORDER BY name, phone_number LIMIT 5")
    suspend fun getCustomersLike(text: String): List<Customer>

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
     * Deletes the [Customer] with the passed id
     *
     * @param contactId the id of the [Customer] to be deleted
     *
     * @return the cout of [Customer]s delete, should always be time
     * */
    @Query("DELETE FROM customers WHERE customer_id = :contactId")
    suspend fun deleteById(contactId: Long): Int

    /**
     * Updates the passed [Customer] and returns the count of [Customer]s updated.
     *
     * @param customer the [Customer] which will be updated
     * @return the count of [Customer]s inserted (0 if not updated, 1 if updated)
     * */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(customer: Customer): Int
}
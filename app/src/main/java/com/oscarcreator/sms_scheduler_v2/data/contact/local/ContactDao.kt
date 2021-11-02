package com.oscarcreator.sms_scheduler_v2.data.contact.local

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.room.*
import com.oscarcreator.sms_scheduler_v2.data.contact.Contact

/**
 * A Data access object (Dao) for [Contact] object.
 * */
@Dao
interface ContactDao {

    /**
     * Returns all [Contact] in a [LiveData] object.
     *
     * @return all customers
     * */
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun observeContacts(): LiveData<List<Contact>>

    /**
     * Returns a the [Contact] with the id
     *
     * @param customerId the id of the [Contact] which will be returned
     * @return the [Contact] with the passed id
     * */
    @Query("SELECT * FROM contacts WHERE contact_id = :customerId")
    suspend fun getCustomer(customerId: Long): Contact

    /**
     * Observes the [Contact] with the specified id
     *
     * @param id the id of the [Contact]
     * @return the customer with the specified id
     * */
    @Query("SELECT * FROM contacts WHERE contact_id = :id")
    fun observeCustomer(id: Long): LiveData<Contact>

    /**
     * Returns the first five customers which is matched by name or
     *  phone number with the passed text.
     *
     *  @param text the text which is used to search through customers
     *  @return the matched customers
     * */
    @Query("SELECT * FROM contacts WHERE name LIKE :text OR phone_number LIKE :text ORDER BY name, phone_number LIMIT 5")
    suspend fun getCustomersLike(text: String): List<Contact>

    /**
     * Inserts a [Contact] into the database and returns the id of the inserted [Contact].
     * If a [Contact] already exist's with the passed id then the insert will be ignored.
     *
     * @param contact the [Contact] which will be inserted
     * @return the id of the inserted [Contact]
     * */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contact: Contact): Long

    /**
     * Deletes the passed [Contact]s and returns the count of [Contact]s deleted.
     *
     * @throws SQLiteConstraintException when [Contact] is references in a scheduled treatment
     * @param contact the [Contact]s which will be deleted
     *
     * @return the count of [Contact]s deleted
     * */
    @Delete
    suspend fun delete(vararg contact: Contact): Int

    /**
     * Deletes the [Contact] with the passed id
     *
     * @param contactId the id of the [Contact] to be deleted
     *
     * @return the cout of [Contact]s delete, should always be time
     * */
    @Query("DELETE FROM contacts WHERE contact_id = :contactId")
    suspend fun deleteById(contactId: Long): Int

    /**
     * Updates the passed [Contact] and returns the count of [Contact]s updated.
     *
     * @param contact the [Contact] which will be updated
     * @return the count of [Contact]s inserted (0 if not updated, 1 if updated)
     * */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(contact: Contact): Int
}
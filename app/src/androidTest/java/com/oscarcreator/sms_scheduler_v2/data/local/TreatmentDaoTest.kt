package com.oscarcreator.sms_scheduler_v2.data.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import com.oscarcreator.sms_scheduler_v2.data.treatment.local.TreatmentDao
import com.oscarcreator.sms_scheduler_v2.util.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TreatmentDaoTest : BaseDaoTest() {

    private lateinit var treatmentDao: TreatmentDao

    @Before
    override fun inititalizeDatabase() {
        super.inititalizeDatabase()
        treatmentDao = database.treatmentDao()
    }

    @Test
    fun treatment_insertedInDatabase_returnsTreatment() = runBlocking {
        val treatment = Treatment(name = "Salt", duration = 50, price = 500, treatmentId = 7)
        assertThat(treatmentDao.insert(treatment), `is`(7))

        assertThat(treatmentDao.observeAllTreatments().getOrAwaitValue(), `is`(listOf(treatment)))
    }

    @Test
    fun treatment_insertedAndDeleted_returnsEmpty() = runBlocking {
        val treatment = Treatment(treatmentId = 5, name = "Sausage", duration = 10, price = 5000)
        assertThat(treatmentDao.insert(treatment), `is`(5))
        assertThat(treatmentDao.delete(treatment), `is`(1))

        assertThat(treatmentDao.observeAllTreatments().getOrAwaitValue(), `is`(emptyList()))
    }

    @Test
    fun treatment_deleteMultiple_returnsEmpty() = runBlocking {
        val treatment1 = Treatment(treatmentId = 1, name = "Ham1", duration = 40, price = 100)
        val treatment2 = Treatment(treatmentId = 3, name = "Ham2", duration = 50, price = 200)
        assertThat(treatmentDao.insert(treatment1), `is`(1))
        assertThat(treatmentDao.insert(treatment2), `is`(3))
        assertThat(treatmentDao.delete(treatment1, treatment2), `is`(2))

        assertThat(treatmentDao.observeAllTreatments().getOrAwaitValue(), `is`(emptyList()))
    }

    @Test
    fun treatment_insertDuplicate_isNotInserted() = runBlocking {
        val treatment = Treatment(treatmentId = 1, name = "Ern", duration = 10, price = 1)
        assertThat(treatmentDao.insert(treatment), `is`(1))
        assertThat(treatmentDao.insert(treatment), `is`(-1))

        assertThat(treatmentDao.observeAllTreatments().getOrAwaitValue(), `is`(listOf(treatment)))
    }

    @Test
    fun treatment_inserted_idIsGenerated() = runBlocking {
        val treatment1 = Treatment(name = "Tobby", duration = 3, price = 30000)
        assertThat(treatmentDao.insert(treatment1), `is`(1))
        val treatment2 = Treatment(name = "Tobby2", duration = 3, price = 30000)
        assertThat(treatmentDao.insert(treatment2), `is`(2))
    }

    @Test
    fun treatment_inserted_updateToBeDeleted() = runBlocking {
        val treatment1 = Treatment(name = "Tobby", price = 400, duration = 50, treatmentId = 2)
        treatmentDao.insert(treatment1)
        treatmentDao.updateToBeDeleted(treatment1.treatmentId)

        assertThat(treatmentDao.observeAllTreatments().getOrAwaitValue().first().toBeDeleted, `is`(true))
    }

    @Test
    fun treatment_deleteById() = runBlocking {
        val treatment = Treatment(name = "Tobby", price = 400, duration = 30, treatmentId = 4)
        treatmentDao.insert(treatment)

        assertThat(treatmentDao.observeAllTreatments().getOrAwaitValue(), `is`(listOf(treatment)))

        treatmentDao.deleteById(treatment.treatmentId)

        assertThat(treatmentDao.observeAllTreatments().getOrAwaitValue(), `is`(emptyList()))
    }

}
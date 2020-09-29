package com.oscarcreator.sms_scheduler_v2.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import com.oscarcreator.sms_scheduler_v2.data.treatment.TreatmentDao
import com.oscarcreator.sms_scheduler_v2.util.observeOnce
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
        val treatment = Treatment(id = 7, name = "Salt", duration = 50, price = 500)
        assertThat(treatmentDao.insert(treatment), `is`(7))

        treatmentDao.getTreatments().observeOnce {
            assertThat(it, `is`(listOf(treatment)))
        }
    }

    @Test
    fun treatment_insertedAndDeleted_returnsEmpty() = runBlocking {
        val treatment = Treatment(id = 5, name = "Sausage", duration = 10, price = 5000)
        assertThat(treatmentDao.insert(treatment), `is`(5))
        assertThat(treatmentDao.delete(treatment), `is`(1))

        treatmentDao.getTreatments().observeOnce {
            assertThat(it, `is`(emptyList()))
        }
    }

    @Test
    fun treatment_insertedAndUpdated_returnsUpdated() = runBlocking {
        val treatment = Treatment(id = 4, name = "Ham", duration = 40, price = 100)
        treatmentDao.insert(treatment)
        val updatedTreatment = Treatment(id = 4, name = "Ham", duration = 40, price = 200)
        assertThat(treatmentDao.update(updatedTreatment), `is`(1))

        treatmentDao.getTreatments().observeOnce {
            assertThat(it, `is`(listOf(updatedTreatment)))
        }
    }

    @Test
    fun treatment_deleteMultiple_returnsEmpty() = runBlocking {
        val treatment1 = Treatment(id = 1, name = "Ham1", duration = 40, price = 100)
        val treatment2 = Treatment(id = 3, name = "Ham2", duration = 50, price = 200)
        assertThat(treatmentDao.insert(treatment1), `is`(1))
        assertThat(treatmentDao.insert(treatment2), `is`(3))
        assertThat(treatmentDao.delete(treatment1, treatment2), `is`(2))

        treatmentDao.getTreatments().observeOnce {
            assertThat(it, `is`(emptyList()))
        }
    }

    @Test
    fun treatment_insertDuplicate_isNotInserted() = runBlocking {
        val treatment = Treatment(id = 1, name = "Ern", duration = 10, price = 1)
        assertThat(treatmentDao.insert(treatment), `is`(1))
        assertThat(treatmentDao.insert(treatment), `is`(-1))

        treatmentDao.getTreatments().observeOnce {
            assertThat(it, `is`(listOf(treatment)))
        }
    }

    @Test
    fun treatment_inserted_idIsGenerated() = runBlocking {
        val treatment1 = Treatment(name = "Tobby", duration = 3, price = 30000)
        assertThat(treatmentDao.insert(treatment1), `is`(1))
        val treatment2 = Treatment(name = "Tobby2", duration = 3, price = 30000)
        assertThat(treatmentDao.insert(treatment2), `is`(2))
    }


}
package com.oscarcreator.sms_scheduler_v2.treatment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.oscarcreator.sms_scheduler_v2.FakeTreatmentsRepository
import com.oscarcreator.sms_scheduler_v2.MainCoroutineRule
import com.oscarcreator.sms_scheduler_v2.addedittreatment.AddEditTreatmentViewModel
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import com.oscarcreator.sms_scheduler_v2.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddEditTreatmentViewModelTest {

    private lateinit var addEditTreatmentViewModel: AddEditTreatmentViewModel

    private lateinit var treatmentsRepository: FakeTreatmentsRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        treatmentsRepository = FakeTreatmentsRepository()
        addEditTreatmentViewModel = AddEditTreatmentViewModel(treatmentsRepository)
    }

    @Test
    fun saveTreatmentToRepository_treatmentSaved() {
        val treatment = Treatment("Treatment 1", 400, 30)

        addEditTreatmentViewModel.start()

        addEditTreatmentViewModel.apply {
            name.value = treatment.name
            price.value = treatment.price.toString()
            duration.value = treatment.duration.toString()
        }

        addEditTreatmentViewModel.saveTreatment()

        val newTreatment = treatmentsRepository.treatmentsServiceData.values.first()

        assertThat(newTreatment.name, `is`(treatment.name))
        assertThat(newTreatment.price, `is`(treatment.price))
        assertThat(newTreatment.duration, `is`(treatment.duration))
    }

    @Test
    fun loadTreatment_dataShown() {
        val treatment = Treatment("Treatment 1", 400, 30)

        runBlocking {
            treatmentsRepository.insert(treatment)
        }

        addEditTreatmentViewModel.start(treatment.treatmentId)

        assertThat(addEditTreatmentViewModel.name.getOrAwaitValue(), `is`(treatment.name))
        assertThat(addEditTreatmentViewModel.price.getOrAwaitValue(), `is`(treatment.price.toString()))
        assertThat(addEditTreatmentViewModel.duration.getOrAwaitValue(), `is`(treatment.duration.toString()))
    }

    @Test
    fun saveTreatment_emptyName_error() {
        saveTreatmentAndAssert("", "200", "50", false)
    }

    @Test
    fun saveTreatment_nullName_error() {
        saveTreatmentAndAssert(null, "200", "50", false)
    }

    @Test
    fun saveTreatment_emptyPrice_error() {
        saveTreatmentAndAssert("Treatment 1", "", "50", false)
    }

    @Test
    fun saveTreatment_nullPrice_error() {
        saveTreatmentAndAssert("Treatment 1", null, "50", false)
    }

    @Test
    fun saveTreatment_emptyDuration_error() {
        saveTreatmentAndAssert("Treatment 1", "200", "", false)
    }

    @Test
    fun saveTreatment_nullDuration_error() {
        saveTreatmentAndAssert("Treatment 1", "200", null, false)
    }

    @Test
    fun saveTreatment_validTreatment_valid() {
        saveTreatmentAndAssert("Treatment 1", "200", "40", true)
    }

    private fun saveTreatmentAndAssert(name: String?, price: String?, duration: String?, expectedIsSaved: Boolean) {
        addEditTreatmentViewModel.start()

        addEditTreatmentViewModel.apply {
            this.name.value = name
            this.price.value = price
            this.duration.value = duration
        }

        addEditTreatmentViewModel.saveTreatment()

        assertThat(treatmentsRepository.treatmentsServiceData.values.isNotEmpty(), `is`(expectedIsSaved))
    }

}
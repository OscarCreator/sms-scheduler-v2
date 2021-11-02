package com.oscarcreator.sms_scheduler_v2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.oscarcreator.sms_scheduler_v2.data.Result
import com.oscarcreator.sms_scheduler_v2.data.treatment.Treatment
import com.oscarcreator.sms_scheduler_v2.data.treatment.TreatmentsRepository
import kotlinx.coroutines.runBlocking

class FakeTreatmentsRepository: TreatmentsRepository {

    var treatmentsServiceData: LinkedHashMap<Long, Treatment> = LinkedHashMap()

    private val observableTreatments = MutableLiveData<List<Treatment>>()

    override fun observeAllTreatments(): LiveData<List<Treatment>> {
        runBlocking { observableTreatments.value = treatmentsServiceData.values.toList() }
        return observableTreatments
    }

    override fun observeTreatments(): LiveData<List<Treatment>> {
        runBlocking { observableTreatments.value = treatmentsServiceData.values.toList() }
        return observableTreatments.map { treatments ->
            treatments.filter { !it.toBeDeleted }
        }
    }

    override suspend fun getTreatment(id: Long): Result<Treatment> {
        val treatment = treatmentsServiceData[id]
        return if (treatment == null) {
            Result.Error(Exception("Did not find treatment"))
        } else {
            Result.Success(treatment)
        }
    }

    override fun observeTreatment(id: Long): LiveData<Result<Treatment>> {
        runBlocking { observableTreatments.value = treatmentsServiceData.values.toList() }
        return observableTreatments.map { treatments ->
            val treatment = treatments.find { it.treatmentId == id }
            if (treatment == null) {
                Result.Error(Exception("Did not find treatment"))
            } else {
                Result.Success(treatment)
            }
        }
    }

    override suspend fun insert(treatment: Treatment): Long {
        treatmentsServiceData[treatment.treatmentId] = treatment
        return treatment.treatmentId
    }


    override suspend fun delete(vararg treatments: Treatment): Int {
        for (treatment in treatments) {
            treatmentsServiceData.remove(treatment.treatmentId)
        }
        return treatments.size
    }

    override suspend fun deleteById(id: Long): Int {
        treatmentsServiceData.remove(id)
        return 1
    }

    override suspend fun updateToBeDeleted(id: Long) {
        val treatment = treatmentsServiceData[id]
        if (treatment != null) {
            val newTreatment = Treatment(treatment.name, treatment.price, treatment.duration, toBeDeleted = true)
            treatmentsServiceData[id] = newTreatment
        }

    }

    override suspend fun updateScheduledTreatmentsWithNewTreatment(oldId: Long, newId: Long) {
        TODO("Not yet implemented")
    }
}
package com.oscarcreator.sms_scheduler_v2.data.treatment

class TreatmentRepository private constructor(private val treatmentDao: TreatmentDao) {

    fun getTreatments() = treatmentDao.getTreatments()

    fun getTreatment(id: Long) = treatmentDao.getTreatment(id)

    suspend fun insert(treatment: Treatment) = treatmentDao.insert(treatment)

    suspend fun update(treatment: Treatment) = treatmentDao.update(treatment)

    suspend fun delete(vararg treatments: Treatment) = treatmentDao.delete(*treatments)

}
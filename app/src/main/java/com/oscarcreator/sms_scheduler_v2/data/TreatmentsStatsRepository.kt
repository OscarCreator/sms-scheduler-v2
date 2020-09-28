package com.oscarcreator.sms_scheduler_v2.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TreatmentsStatsRepository private constructor(){

    private var totalTreatments = object : MutableLiveData<Int>(0){}
    private var procentTreatments = object : MutableLiveData<Int>(0){}
    private var totalEarnings = object : MutableLiveData<Int>(0){}
    private var totalEarningsExclusive = object : MutableLiveData<Int>(0){}

    fun getTotalTreatments(): LiveData<Int> {
        return totalTreatments
    }

    fun setTotalTreatments(totalTreatments: Int){
        this.totalTreatments.postValue(totalTreatments)
    }

    fun getProcentTreatments(): LiveData<Int> {
        return procentTreatments
    }

    fun setProcentTreatments(procentTreatments: Int){
        this.procentTreatments.postValue(procentTreatments)
    }

    fun getTotalEarnings(): LiveData<Int> {
        return this.totalEarnings
    }

    fun setTotalEarnings(totalEarnings: Int){
        this.totalEarnings.postValue(totalEarnings)
    }

    fun getTotalEarningsExclusive(): LiveData<Int> {
        return totalEarningsExclusive
    }

    fun setTotalEarningsExclusive(totalEarningsExclusive: Int){
        this.totalEarningsExclusive.postValue(totalEarningsExclusive)
    }

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: TreatmentsStatsRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: TreatmentsStatsRepository().also { instance = it }
            }
    }

}

package com.oscarcreator.pigeon.dashboard

import androidx.lifecycle.ViewModel
import com.oscarcreator.pigeon.data.TreatmentsStatsRepository

class DashboardViewModel(
    private val treatmentsStatsRepository: TreatmentsStatsRepository
) : ViewModel() {

    var totalTreatments = treatmentsStatsRepository.getTotalTreatments()
    var procentTreatments = treatmentsStatsRepository.getProcentTreatments()
    var totalEarnings = treatmentsStatsRepository.getTotalEarnings()
    var totalEarningsExclusive = treatmentsStatsRepository.getTotalEarningsExclusive()

    fun setTotalTreatments(totalTreatments: Int){
        treatmentsStatsRepository.setTotalTreatments(totalTreatments)
    }

    fun setProcentTreatments(procentTreatments: Int){
        treatmentsStatsRepository.setProcentTreatments(procentTreatments)
    }

    fun setTotalEarnings(totalEarnings: Int){
        treatmentsStatsRepository.setTotalEarnings(totalEarnings)
    }

    fun setTotalEarningsExclusive(totalEarningsExclusive: Int){
        treatmentsStatsRepository.setTotalEarningsExclusive(totalEarningsExclusive)
    }

}
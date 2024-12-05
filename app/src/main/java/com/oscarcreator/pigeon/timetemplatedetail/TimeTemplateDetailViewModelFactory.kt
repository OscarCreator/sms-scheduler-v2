package com.oscarcreator.pigeon.timetemplatedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.pigeon.data.timetemplate.TimeTemplatesRepository

class TimeTemplateDetailViewModelFactory(
    private val timeTemplatesRepository: TimeTemplatesRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TimeTemplateDetailViewModel(timeTemplatesRepository) as T
    }
}
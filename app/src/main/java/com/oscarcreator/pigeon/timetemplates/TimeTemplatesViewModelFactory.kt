package com.oscarcreator.pigeon.timetemplates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.pigeon.data.timetemplate.TimeTemplatesRepository

class TimeTemplatesViewModelFactory(
    private val timeTemplatesRepository: TimeTemplatesRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TimeTemplatesViewModel(timeTemplatesRepository) as T
    }
}
package com.oscarcreator.sms_scheduler_v2.addedittimetemplate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.sms_scheduler_v2.data.timetemplate.TimeTemplatesRepository

class AddEditTimeTemplateViewModelFactory(
    private val timeTemplatesRepository: TimeTemplatesRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddEditTimeTemplateViewModel(timeTemplatesRepository) as T
    }
}
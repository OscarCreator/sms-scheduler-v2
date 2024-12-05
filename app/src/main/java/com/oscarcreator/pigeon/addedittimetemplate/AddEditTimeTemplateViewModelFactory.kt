package com.oscarcreator.pigeon.addedittimetemplate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.pigeon.data.timetemplate.TimeTemplatesRepository

class AddEditTimeTemplateViewModelFactory(
    private val timeTemplatesRepository: TimeTemplatesRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddEditTimeTemplateViewModel(timeTemplatesRepository) as T
    }
}
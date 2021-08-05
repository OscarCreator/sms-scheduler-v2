package com.oscarcreator.sms_scheduler_v2.addeditcontact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oscarcreator.sms_scheduler_v2.data.customer.CustomersRepository

class AddEditContactViewModelFactory(
    private val customersRepository: CustomersRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddEditContactViewModel(
            customersRepository
        ) as T
    }
}
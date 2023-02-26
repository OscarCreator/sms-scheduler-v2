package com.oscarcreator.sms_scheduler_v2.util

import androidx.lifecycle.*

class OneTimeObserver<T>(private val handler: (T) -> Unit) : Observer<T>, LifecycleOwner {
    override val lifecycle = LifecycleRegistry(this)

    init {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onChanged(value: T) {
        handler(value)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }


}

fun <T> LiveData<T>.observeOnce(onChangeHandler: (T) -> Unit) {
    val observer =
        OneTimeObserver(handler = onChangeHandler)
    observe(observer, observer)
}
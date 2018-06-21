package com.kalk.jmr.ui.settings

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class SettingsViewModel: ViewModel() {

    val time: MutableLiveData<Boolean> = MutableLiveData()
    val location: MutableLiveData<Boolean> = MutableLiveData()
    val activity: MutableLiveData<Boolean> = MutableLiveData()

    init {
        time.value = false
        location.value = false
        activity.value = false
    }
}
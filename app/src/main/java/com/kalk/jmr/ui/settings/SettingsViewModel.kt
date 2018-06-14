package com.kalk.jmr.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel: ViewModel() {

    val time: MutableLiveData<Boolean> = MutableLiveData()
    val location: MutableLiveData<Boolean> = MutableLiveData()
    val activity: MutableLiveData<Boolean> = MutableLiveData()
}
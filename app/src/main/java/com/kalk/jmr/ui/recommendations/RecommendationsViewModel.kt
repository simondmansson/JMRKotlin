package com.kalk.jmr.ui.recommendations

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.kalk.jmr.db.genre.Genre

class RecommendationsViewModel : ViewModel() {
    private val songs: MutableLiveData<List<String>> = MutableLiveData()

    private val currentActivity: MutableLiveData<String> = MutableLiveData()
    private val currentLocation: MutableLiveData<String> = MutableLiveData()
    val authToken: MutableLiveData<Token> = MutableLiveData()
    val genres:MutableLiveData<List<Genre>> = MutableLiveData()


    fun getSongs(): LiveData<List<String>> {
        songs.value?.size ?: songs.postValue(listOf("3IDsegNBHC4pjGCOMTQYlU", "1iGXvUsVVkYBas0Cniw6NB", "2NJ3P3vXdOBk9cjPIFS3uH"))
        return songs
    }



    fun setActivity(current: String) { currentActivity.value = current }
    fun getCurrentActivity(): LiveData<String> { return currentActivity }

    fun setLocation(current: String) { currentLocation.value = current }
    fun getCurrentLocation(): LiveData<String> { return currentLocation }
}

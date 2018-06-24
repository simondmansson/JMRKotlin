package com.kalk.jmr.ui.recommendations

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.kalk.jmr.db.PlaylistRepository
import com.kalk.jmr.db.location.UserLocation
import com.kalk.jmr.db.track.Track
import java.util.*

class RecommendationsViewModel(val repo: PlaylistRepository) : ViewModel() {

    private val songs: MutableLiveData<List<String>> = MutableLiveData()

    val currentActivity: MutableLiveData<String> = MutableLiveData()
    val currentLocation: MutableLiveData<UserLocation> = MutableLiveData()
    val authToken: MutableLiveData<Token> = MutableLiveData()

    val currentLocationText: LiveData<String> = Transformations.map(currentLocation, {
            "${it.coordinates.longitude} ${it.coordinates.latitude}"
    })

    fun getSongs(): LiveData<List<String>> {
        songs.value?.size ?: songs.postValue(listOf("3IDsegNBHC4pjGCOMTQYlU", "1iGXvUsVVkYBas0Cniw6NB", "2NJ3P3vXdOBk9cjPIFS3uH"))
        return songs
    }

    fun setActivity(current: String) { currentActivity.value = current }
    fun getCurrentActivity(): LiveData<String> { return currentActivity }

    fun setLocation(current: UserLocation) { currentLocation.postValue(current)}
    fun getCurrentLocation(): LiveData<UserLocation> { return currentLocation }

    fun makeRecommendation(): List<String> {
        val recommendation = getTracks()

        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        //repo.storePlaylist(currentActivity, currentLocation, hourOfDay, recommendation)

        return recommendation.map { it.uri }
    }

    fun getTracks() :List<Track>  {
        return listOf(Track("3IDsegNBHC4pjGCOMTQYlU", "Baby, Baby - AmyGrant"),
                Track("1iGXvUsVVkYBas0Cniw6NB", "Schokwave Gesaffelstein Remix - The Hacker"))
    }
}

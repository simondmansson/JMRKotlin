package com.kalk.jmr.ui.recommendations

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.kalk.jmr.db.PlaylistRepository
import com.kalk.jmr.db.location.UserLocation
import com.kalk.jmr.db.playlist.Playlist
import com.kalk.jmr.db.track.Track
import com.kalk.jmr.db.userActivity.UserActivity
import java.util.*

class RecommendationsViewModel(val repo: PlaylistRepository) : ViewModel() {

    val currentActivity: MutableLiveData<UserActivity> = MutableLiveData()
    val currentActivityText: LiveData<String> = Transformations.map(currentActivity, { it.type })
    val currentLocation: MutableLiveData<UserLocation> = MutableLiveData()
    val authToken: MutableLiveData<Token> = MutableLiveData()

    val currentLocationText: LiveData<String> = Transformations.map(currentLocation, {
            "${it.coordinates.longitude} ${it.coordinates.latitude}"
    })

    fun setActivity(current: UserActivity) { currentActivity.value = current }

    fun setLocation(current: UserLocation) { currentLocation.postValue(current)}
    fun getCurrentLocation(): LiveData<UserLocation> { return currentLocation }

    fun makeRecommendation(genre:Int): List<String> {
        val recommendations = getTracks()

        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        val playlist = Playlist(
                UUID.randomUUID().toString(),
                "${currentActivityText.value} in ${currentLocationText.value}, ${dayOfWeek(day)} at $hourOfDay",
                currentLocation.value!!.id,
                currentActivity.value!!.id,
                genre,
                hourOfDay)

        repo.storePlaylist(playlist, recommendations)

        return recommendations.map { it.uri }
    }

    fun getTracks() :List<Track>  {
        return listOf(Track("3IDsegNBHC4pjGCOMTQYlU", "Baby, Baby - AmyGrant"),
                Track("1iGXvUsVVkYBas0Cniw6NB", "Schokwave Gesaffelstein Remix - The Hacker"))
    }

   private fun dayOfWeek(day:Int):String {
        when(day) {
            1 -> return "Sunday"
            2 -> return "Monday"
            3 -> return "Tuesday"
            4 -> return "Wednesday"
            5 -> return "Thursday"
            6 -> return "Friday"
            else -> return "Saturday"
        }
    }
}

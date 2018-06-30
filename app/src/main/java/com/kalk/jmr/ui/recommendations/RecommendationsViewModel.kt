package com.kalk.jmr.ui.recommendations

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.kalk.jmr.db.PlaylistRepository
import com.kalk.jmr.db.location.UserLocation
import com.kalk.jmr.db.playlist.Playlist
import com.kalk.jmr.db.track.Track
import com.kalk.jmr.db.userActivity.UserActivity
import java.util.*
import kotlin.collections.ArrayList

class RecommendationsViewModel(val repo: PlaylistRepository) : ViewModel() {
    val TAG = RecommendationsViewModel::class.java.simpleName
    val currentActivity: MutableLiveData<UserActivity> = MutableLiveData()
    val currentActivityText: LiveData<String> = Transformations.map(currentActivity, { it.type })
    val currentLocation: MutableLiveData<UserLocation> = MutableLiveData()
    val authToken: MutableLiveData<Token> = MutableLiveData()

    val currentLocationText: LiveData<String> = Transformations.map(currentLocation, {
            "${it.coordinates.longitude} ${it.coordinates.latitude}"
    })

    fun setActivity(current: UserActivity) { currentActivity.value = current }

    fun setLocation(current: UserLocation) { currentLocation.postValue(current) }
    fun getCurrentLocation(): LiveData<UserLocation> { return currentLocation }

    fun makeRecommendationFromGenre(genreText:String) = repo.requestTracksFromGenre(authToken.value?.token ?: "", genreText)

    fun makeRecommendationFromHistory(uris: List<String>) = repo.requestTracksFromHistory(authToken.value?.token ?: "", uris)

    /**
     * THIS IS ONE CLUSTERFUCK OF A THING - SIMON 30/18 - 22:41
     */
    fun tracksConnectedToContext(checkForActivity:Boolean, checkForLocation:Boolean, checkForTime:Boolean): ArrayList<String> {
        // -1 || empty string == don't query for this value
        val plIds = repo.getPlaylistIdsFromContextParameters(
                location = if (checkForLocation) currentLocation.value!!.id else "",
                activity = if(checkForActivity) currentActivity.value!!.id else -1,
                time = if(checkForTime) Date().toString().substring(11, 13).toInt() /*hour*/ else -1
                )
        Log.i(TAG, "plIDs $plIds")
        val tracks = repo.getTrackURisFromPlaylistIds(plIds)
        return ArrayList(tracks)
    }

    fun storePlaylist(genreId: Int, tracks: List<Track>) {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val day = calendar.get(Calendar.DAY_OF_WEEK)

        val playlist = Playlist(
                UUID.randomUUID().toString(),
                "${currentActivityText.value} in ${currentLocationText.value}, ${dayOfWeek(day)} at $hourOfDay",
                currentLocation.value!!.id,
                currentActivity.value!!.id,
                genreId,
                hourOfDay)
        repo.storePlaylist(playlist, tracks)
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

package com.kalk.jmr.db

import android.util.Log
import com.kalk.jmr.buildJMRWebService
import com.kalk.jmr.db.playlist.Playlist
import com.kalk.jmr.db.playlist.PlaylistDao
import com.kalk.jmr.db.playlistTracks.PlaylistTrack
import com.kalk.jmr.db.playlistTracks.PlaylistTracksDao
import com.kalk.jmr.db.track.Track
import com.kalk.jmr.db.track.TrackDao
import com.kalk.jmr.ioThread
import com.kalk.jmr.webService.GenreMessage
import com.kalk.jmr.webService.HistoryMessage
import com.kalk.jmr.webService.JMRWebService

class PlaylistRepository private constructor(private val trackDao: TrackDao,
                                             private val playlistTracksDao: PlaylistTracksDao,
                                             private val playlistDao: PlaylistDao) {

    val storedPlaylists = playlistDao.selectAll()
    private val service: JMRWebService

    private val NOACTIVITY = -1
    private val NOTIME = -1

    init {
        ioThread {
            Log.i(TAG, "stored tracks ${trackDao.allTracks()}")
            Log.i(TAG, "playlist tracks ${playlistTracksDao.selectAll()}")
            Log.i(TAG, "stored tracks ${playlistDao.selectAll()}")
        }
        service = buildJMRWebService()
    }

    fun getTracksFromPlaylistId(id: String) = playlistTracksDao.findTracksbyId(id)

    fun getTrackURisFromPlaylistIds(playlistIds: List<String>) = playlistTracksDao.findPlaylistUrisByIds(playlistIds)
    /**
     * WHERE DA UNIT TEST AT?!
     */
    fun getPlaylistIdsFromContextParameters(location: String, activity: Int, time: Int): List<String> {
        Log.i(TAG, "Params: $location, $activity, $time")
        var ids: List<String> = listOf()
        playlistDao.apply {
        when {
            //SINGLE PARAM
            location.isNotEmpty() && activity == NOACTIVITY && time == NOTIME -> { ids = selectByLocation(location); Log.i(TAG, "LOCATION") }
            location.isEmpty() && activity == NOACTIVITY && time != NOTIME -> { ids = selectByTime(time-1, time+1); Log.i(TAG, "TIME") }
            location.isEmpty() && activity != NOACTIVITY && time == NOTIME -> { ids = selectByActivity(activity) ; Log.i(TAG, "ACTIVITY") }
            //PAIR OF PARAMS
            location.isNotEmpty() && activity == NOACTIVITY && time != NOTIME -> { ids = selectByLocationAndTime(location, time-1, time+1); Log.i(TAG, "LOCATION AND TIME")}
            location.isNotEmpty() && activity != NOACTIVITY && time == NOTIME -> { ids = selectByLocationAndActivity(location, activity); Log.i(TAG, "LOCATION AND ACTIVITY") }
            location.isEmpty() && activity != NOACTIVITY && time != NOTIME -> { ids = selectByActivityAndTime(activity, time-1, time+1); Log.i(TAG, "ACTIVITY AND TIME") }
            //TRIO
            else-> { ids = selectByLocationAndActivityAndTime(location, activity, time-1, time+1); Log.i(TAG, "ALL THREE")}
            }
        }

        return ids
    }

    fun removePlaylist(id:String){
        ioThread {
            playlistDao.removePlaylist(id)
        }
    }

    fun removeTrackFromPlaylist(playlistId:String ,trackUri:String) {
        ioThread {
            playlistTracksDao.removePlaylistTrack(PlaylistTrack(playlistId, trackUri))
        }
    }

    fun requestTracksFromGenre(token:String, genre: String): List<Track> {
        val message = GenreMessage(token, genre.decapitalize())
        Log.i(TAG, message.toString())
        val res = service.postRecommendationGenre(message).execute()
        if(res.isSuccessful && res.body() != null) {
            val tracks = res.body()
            return tracks!!
        } else  {
            Log.e(TAG, " Retrieving from genre failed. Error msg: ${res.body()} ${res.code()}")
        }
        return listOf()
    }

    fun requestTracksFromHistory(token:String, uirs:List<String>):  List<Track> {
        val message = HistoryMessage(token, uirs)
        Log.i(TAG, message.toString())
        val res = service.postRecommendationHistory(message).execute()
        if(res.isSuccessful && res.body() != null) {
            val tracks = res.body()
            return tracks!!
        } else  {
            Log.e(TAG, " Retrieving from history failed. Error msg: ${res.body()} ${res.code()}")
        }
        return listOf()
    }

   fun storePlaylist(playlist: Playlist, tracks:List<Track>) {
        ioThread {
            Log.i(TAG, "adding ${playlist.title}  with # of tracks ${tracks.size}")
            tracks.forEach {
                trackDao.addTrack(it)
                playlistTracksDao.addPlaylistTrack(PlaylistTrack(playlist.id, it.uri))
            }
            playlistDao.addPlaylist(playlist)
        }
    }

    companion object {
        private var sInstance: PlaylistRepository? = null
        fun getInstance(trackDao: TrackDao, playlistTracksDao: PlaylistTracksDao, playlistDao: PlaylistDao) = sInstance ?:
        synchronized(this) {
            sInstance ?: buildRepository(trackDao, playlistTracksDao, playlistDao).also { sInstance = it }
        }

        private fun buildRepository(trackDao: TrackDao, playlistTracksDao: PlaylistTracksDao, playlistDao: PlaylistDao): PlaylistRepository {
            return PlaylistRepository(trackDao, playlistTracksDao, playlistDao)
        }

        val TAG = PlaylistRepository::class.java.simpleName
    }
}
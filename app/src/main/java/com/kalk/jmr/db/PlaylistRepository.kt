package com.kalk.jmr.db

import android.arch.lifecycle.MutableLiveData
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
import com.kalk.jmr.webService.JMRWebService

class PlaylistRepository private constructor(private val trackDao: TrackDao,
                                             private val playlistTracksDao: PlaylistTracksDao,
                                             private val playlistDao: PlaylistDao) {


    val trackCount: MutableLiveData<Int> = MutableLiveData()
    val storedPlaylists = playlistDao.selectAll()
    private val service: JMRWebService

    init {
        ioThread {
            Log.i(TAG, "stored tracks ${trackDao.allTracks()}")
            Log.i(TAG, "playlistracks ${playlistTracksDao.selectAll()}")
            Log.i(TAG, "stored tracks ${playlistDao.selectAll()}")
        }
        service = buildJMRWebService()
    }

    fun getTracksFromPlaylistId(id: String) = playlistTracksDao.findTracksbyId(id)

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
            Log.e(TAG, " Retrieving from genre failed. Error msg: ${res.errorBody().toString()} ${res.code()}")
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
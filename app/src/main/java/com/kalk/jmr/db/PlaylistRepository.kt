package com.kalk.jmr.db

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.kalk.jmr.db.playlist.HistoryPlaylist
import com.kalk.jmr.db.playlist.PlaylistDao
import com.kalk.jmr.db.playlistTracks.PlaylistTracksDao
import com.kalk.jmr.db.track.TrackDao
import com.kalk.jmr.ioThread

class PlaylistRepository private constructor(val trackDao: TrackDao, val playlistTracksDao: PlaylistTracksDao, val playlistDao: PlaylistDao) {

    private var playlists:MutableLiveData<List<HistoryPlaylist>> = MutableLiveData()

    init {
        ioThread {
            val storedPlaylists = playlistDao.selectAll()
            val hpls = storedPlaylists.map {
                val songs = playlistTracksDao.findTracksbyId(it.id)
                HistoryPlaylist(it.title, songs)
            }
            playlists.postValue(hpls)
        }

    }

    fun getPlaylists(): LiveData<List<HistoryPlaylist>> {
        return playlists
    }

    fun addPlaylist() {

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
    }
}
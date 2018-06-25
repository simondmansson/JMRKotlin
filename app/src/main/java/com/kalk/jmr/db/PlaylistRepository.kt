package com.kalk.jmr.db

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.kalk.jmr.db.playlist.HistoryPlaylist
import com.kalk.jmr.db.playlist.Playlist
import com.kalk.jmr.db.playlist.PlaylistDao
import com.kalk.jmr.db.playlistTracks.PlaylistTrack
import com.kalk.jmr.db.playlistTracks.PlaylistTracksDao
import com.kalk.jmr.db.track.Track
import com.kalk.jmr.db.track.TrackDao
import com.kalk.jmr.ioThread


class PlaylistRepository private constructor(val trackDao: TrackDao, val playlistTracksDao: PlaylistTracksDao, val playlistDao: PlaylistDao) {

    private var storedPlaylists = playlistDao.selectAll()
    private var storedTracks = trackDao.allTracks()
    private var storedPlaylistTracks = playlistTracksDao.selectAll()

    private val playlists: LiveData<List<HistoryPlaylist>> = Transformations.map(storedPlaylists, {
        it.map { playlist ->
            val songsUris = storedPlaylistTracks.value?.filter { playlist.id == it.playlist }
            val songs: MutableList<Track> = mutableListOf()
            songsUris?.forEach {plTrack ->
                val track = storedTracks.value?.find { it.uri == plTrack.track }
                if (track != null)
                    songs.add(track)
            }
            HistoryPlaylist(playlist.title, songs)
        }
    })

    fun getPlaylists(): LiveData<List<HistoryPlaylist>> {
        return playlists
    }

    fun storePlaylist(playlist: Playlist, tracks:List<Track>) {
        ioThread {
            playlistDao.addPlaylist(playlist)
            tracks.forEach {
                trackDao.addTrack(it)
                playlistTracksDao.addPlaylistTrack(PlaylistTrack(playlist.id, it.uri))
            }
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
    }
}
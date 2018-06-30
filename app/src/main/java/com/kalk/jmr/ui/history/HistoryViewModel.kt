package com.kalk.jmr.ui.history

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.kalk.jmr.db.PlaylistRepository
import com.kalk.jmr.db.playlist.Playlist


class HistoryViewModel internal constructor(private val repo: PlaylistRepository): ViewModel() {

    val playlists: LiveData<List<Playlist>> = Transformations.map(repo.storedPlaylists ) { it }

    fun getTracksFromPlaylistId(id:String) = repo.getTracksFromPlaylistId(id)

    fun removePlaylist(id:String) = repo.removePlaylist(id)
}
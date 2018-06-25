package com.kalk.jmr.ui.history

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.kalk.jmr.db.PlaylistRepository
import com.kalk.jmr.db.playlist.HistoryPlaylist

class HistoryViewModel internal constructor(private val repo: PlaylistRepository): ViewModel() {

    val playlists: LiveData<List<HistoryPlaylist>> = Transformations.map(repo.getPlaylists()) { it }

    fun removePlaylist(id:String) = repo.removePlaylist(id)
}
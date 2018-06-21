package com.kalk.jmr.ui.history

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.kalk.jmr.db.playlist.Playlist

class HistoryViewModel: ViewModel() {
    val playlists: MutableLiveData<List<Playlist>> = MutableLiveData()

    init {
        playlists.postValue(loadPlaylists())
    }

    fun getPlaylists(): LiveData<List<Playlist>> {
        playlists.value?.size ?: playlists.postValue(loadPlaylists())
        return playlists
    }

    private fun loadPlaylists(): List<Playlist> {
        return listOf()
    }
}
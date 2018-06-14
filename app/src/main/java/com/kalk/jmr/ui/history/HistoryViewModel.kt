package com.kalk.jmr.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

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
        return listOf(
                Playlist(1,"Malmö-morning-run", arrayListOf("1iGXvUsVVkYBas0Cniw6NB", "3IDsegNBHC4pjGCOMTQYlU", "2NJ3P3vXdOBk9cjPIFS3uH")),
                Playlist(2, "Malmö-afternoon-walk", arrayListOf("50LgxH3t8vWy5xMFljdUbF", "43G2Go5tmms3DjC2qdKCD0")),
                Playlist(3, "Malmö-morning-still", arrayListOf("4rXEQ0gVrddOY4LWUknZt3")))
    }
}
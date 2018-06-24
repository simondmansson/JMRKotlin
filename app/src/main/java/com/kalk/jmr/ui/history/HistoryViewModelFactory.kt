package com.kalk.jmr.ui.history

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kalk.jmr.db.PlaylistRepository

class HistoryViewModelFactory(private val playlistRepository: PlaylistRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistoryViewModel(playlistRepository) as T
    }
}
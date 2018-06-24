package com.kalk.jmr.ui.recommendations

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kalk.jmr.db.PlaylistRepository

class RecommendationsViewModelFactory internal constructor(private val playlistRepository: PlaylistRepository): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecommendationsViewModel(playlistRepository) as T
    }
}
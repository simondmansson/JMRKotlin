package com.kalk.jmr.ui.genres

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kalk.jmr.db.genre.GenreRepository

class GenresViewModelFactory(private val genreRepository: GenreRepository): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GenresViewModel(genreRepository) as T
    }
}
package com.kalk.jmr.ui.recommendations

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.kalk.jmr.db.RecommendationsRepository
import com.kalk.jmr.ui.genres.GenresViewModel

class RecommendationsViewModelFactory internal constructor(private val recommendationsRepository: RecommendationsRepository): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecommendationsViewModel(recommendationsRepository) as T
    }
}
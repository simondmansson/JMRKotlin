package com.kalk.jmr.ui.genres

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.kalk.jmr.db.genre.Genre
import com.kalk.jmr.db.genre.GenreRepository

class GenresViewModel internal constructor(private val repo: GenreRepository): ViewModel() {

    val genres: LiveData<List<Genre>> = Transformations.map(repo.getGenres()) { it }
    val chosenGenre: MutableLiveData<Int> = MutableLiveData()
    val genreText: LiveData<String> = Transformations.map(chosenGenre, {
         genres.value?.get(chosenGenre.value ?: 0)?.genre
    })
}
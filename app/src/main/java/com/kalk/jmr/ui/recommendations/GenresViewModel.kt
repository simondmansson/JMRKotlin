package com.kalk.jmr.ui.recommendations

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.kalk.jmr.db.genre.Genre
import com.kalk.jmr.db.genre.GenreRepository

class GenresViewModel internal constructor(private val repo: GenreRepository): ViewModel() {

    val genres: LiveData<List<Genre>> = Transformations.map(repo.getGenres()) { it }
    private val chosenGenre: MutableLiveData<Int> = MutableLiveData()
    val genreText: LiveData<String> = Transformations.map(chosenGenre, {
         genres.value!![chosenGenre.value!!].genre
    })

    fun setGenre(genre: Int) {
        chosenGenre.value = genre

    }

}
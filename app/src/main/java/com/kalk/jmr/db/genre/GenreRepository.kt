package com.kalk.jmr.db.genre

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.kalk.jmr.ioThread

class GenreRepository private constructor(private val genreDao: GenreDao)  {
    private val genres: MutableLiveData<List<Genre>> = MutableLiveData()

    init {
        ioThread {
            genres.postValue(genreDao.getAllGenres())
        }
    }

    companion object {
        private var sInstance: GenreRepository? = null
        fun getInstance(genreDao: GenreDao) = sInstance ?:
        synchronized(this) {
            sInstance ?: buildRepository(genreDao).also { sInstance = it }
        }

        private fun buildRepository(genreDao: GenreDao): GenreRepository {
            return GenreRepository(genreDao)
        }
    }

    fun getGenres(): LiveData<List<Genre>> {
       return genres
    }
}
package com.kalk.jmr.db.genre

class GenreRepository private constructor(private val genreDao: GenreDao)  {


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

    fun getGenres() = genreDao.getAllGenres()
}
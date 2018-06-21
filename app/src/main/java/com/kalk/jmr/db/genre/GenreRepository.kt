package com.kalk.jmr.db.genre

import com.kalk.jmr.ioThread

class GenreRepository(val genreDao: GenreDao)  {

    fun getGenres(): List<Genre> {
        var genres: List<Genre> = listOf()
        ioThread {
            genres = genreDao.getAllGenres()
        }
        return genres
    }
}
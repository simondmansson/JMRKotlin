package com.kalk.jmr.db.genre

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface GenreDao {
    @Query("Select * from genres where id = :id")
    fun byId(id: Int): Genre

    @Query("Select * from genres")
    fun getAllGenres(): List<Genre>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addGenre(genre: Genre)
}
package com.kalk.jmr.db.genre

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GenreDao {
    @Query("Select * from genres where id = :id")
    fun byId(id: Int): Genre

    @Insert
    fun addGenre(genre: Genre)
}
package com.kalk.jmr.db.track

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TrackDao {
    @Query("Select * from tracks where id = :id")
    fun byId(id: Int): Track

    @Insert
    fun addTrack(track: Track)
}
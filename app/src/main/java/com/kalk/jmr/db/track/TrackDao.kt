package com.kalk.jmr.db.track
import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
@Dao
interface TrackDao {
    @Query("Select * from tracks where uri = :uri")
    fun byUri(uri: String): Track

    @Query("Select * from tracks")
    fun allTracks(): List<Track>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTrack(track: Track)
}
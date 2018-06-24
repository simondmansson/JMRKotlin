package com.kalk.jmr.db.track
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
@Dao
interface TrackDao {
    @Query("Select * from tracks where uri = :uri")
    fun byUri(uri: String): Track

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addTrack(track: Track)
}
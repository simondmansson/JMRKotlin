package com.kalk.jmr.db.playlistTracks

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.kalk.jmr.db.track.Track

@Dao
interface PlaylistTracksDao {

    @Query("Select tracks.uri, tracks.name from tracks inner join playlistTracks on playlistTracks.track = tracks.uri where playlistTracks.playlist = :playlist")
    fun findTracksbyId(playlist: String) :List<Track>

    @Query("Select * from playlistTracks")
    fun selectAll(): LiveData<List<PlaylistTrack>>

    @Insert
    fun addPlaylistTrack(playlistTrack: PlaylistTrack)
}
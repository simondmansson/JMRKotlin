package com.kalk.jmr.db.playlistTracks

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.kalk.jmr.db.track.Track

@Dao
interface PlaylistTracksDao {

    @Query("Select tracks.id, tracks.uri, tracks.title from tracks inner join playlistTracks on playlistTracks.track = tracks.id where playlistTracks.playlist = :playlist")
    fun findTracksbyId(playlist: Int) :List<Track>

    @Insert
    fun addPlaylistTrack(playlistTrack: PlaylistTrack)
}
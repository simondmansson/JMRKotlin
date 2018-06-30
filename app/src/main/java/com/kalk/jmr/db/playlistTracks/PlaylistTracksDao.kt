package com.kalk.jmr.db.playlistTracks

import android.arch.persistence.room.*
import com.kalk.jmr.db.track.Track

@Dao
interface PlaylistTracksDao {

    @Query("Select tracks.uri, tracks.name from tracks inner join playlistTracks on playlistTracks.track = tracks.uri where playlistTracks.playlist = :playlist")
    fun findTracksbyId(playlist: String) :List<Track>

    @Query("select track from playlistTracks where playlist IN(:playlistIds)")
    fun findPlaylistUrisByIds(playlistIds:List<String>): List<String>

    @Query("Select * from playlistTracks")
    fun selectAll(): List<PlaylistTrack>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPlaylistTrack(playlistTrack: PlaylistTrack)

    @Delete
    fun removePlaylistTrack(playlistTrack: PlaylistTrack)
}
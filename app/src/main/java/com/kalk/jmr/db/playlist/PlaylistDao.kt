package com.kalk.jmr.db.playlist
import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface PlaylistDao {
    @Query ("Select * from playlists")
    fun selectAll():LiveData<List<Playlist>>

    @Query ("Select * from playlists where genre = :genre")
    fun selectByGenre(genre: Int) :List<Playlist>

    @Query ("Select * from playlists where location = :location")
    fun selectByLocation(location: String) :List<Playlist>

    @Query ("Select * from playlists where time = :time")
    fun selectByTime(time: Int) :List<Playlist>

    @Query ("Select * from playlists where activity = :activity")
    fun selectByActivity(activity: Int) :List<Playlist>

    @Query ("Select * from playlists where genre = :genre and location = :location")
    fun selectByGenreAndLocation(genre: Int, location: String) :List<Playlist>

    @Query ("Select * from playlists where genre = :genre and time = :time")
    fun selectByGenreAndTime(genre: Int, time: Int) :List<Playlist>

    @Query ("Select * from playlists where genre = :genre and activity = :activity")
    fun selectByGenreAndActivity(genre: Int, activity: Int) :List<Playlist>

    @Query ("""Select * from playlists where
        genre = :genre and location = :location and time = :time""")
    fun selectByGenreAndLocationAndTime(genre: Int, location: String, time:Int) :List<Playlist>

    @Query ("""Select * from playlists where genre = :genre
        and location = :location and activity = :activity""")
    fun selectByGenreAndLocationAndActivity(genre: Int, location: String, activity: Int) :List<Playlist>


    @Query ("""Select * from playlists where genre = :genre and
        location = :location and activity = :activity and time = :time""")
    fun selectByGenreAndLocationAndActivityAndTime(genre: Int, location: String,
                                                   activity: Int, time: Int) :List<Playlist>

    @Insert
    fun addPlaylist(playlist: Playlist)

    @Query("Delete from playlists where id = :id")
    fun removePlaylist(id: String)

}
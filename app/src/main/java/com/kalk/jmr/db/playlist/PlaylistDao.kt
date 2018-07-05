package com.kalk.jmr.db.playlist
import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface PlaylistDao {

    @Insert
    fun addPlaylist(playlist: Playlist)

    @Query("Delete from playlists where id = :id")
    fun removePlaylist(id: String)

    @Query ("Select * from playlists")
    fun selectAll():LiveData<List<Playlist>>

    @Query ("Select id from playlists where genre = :genre")
    fun selectByGenre(genre: Int) :List<String>

    @Query ("Select id from playlists where location = :location")
    fun selectByLocation(location: String) :List<String>

    @Query ("Select id from playlists where time between :timeFrom and :timeTo")
    fun selectByTime(timeFrom: Int, timeTo: Int) :List<String>

    @Query ("Select id from playlists where activity = :activity")
    fun selectByActivity(activity: Int) :List<String>

    @Query ("Select id from playlists where genre = :genre and location = :location")
    fun selectByGenreAndLocation(genre: Int, location: String) :List<String>

    @Query ("Select id from playlists where genre = :genre and time = :time")
    fun selectByGenreAndTime(genre: Int, time: Int) :List<String>

    @Query ("Select id from playlists where genre = :genre and activity = :activity")
    fun selectByGenreAndActivity(genre: Int, activity: Int) :List<String>

    @Query ("Select id from playlists where location = :location and time between :timeFrom and :timeTo ")
    fun selectByLocationAndTime(location: String, timeFrom:Int, timeTo:Int) :List<String>

    @Query ("Select id from playlists where activity = :activity and time between :timeFrom and :timeTo ")
    fun selectByActivityAndTime(activity: Int, timeFrom: Int, timeTo: Int) :List<String>

    @Query ("""Select id from playlists where
        genre = :genre and location = :location and time = :time""")
    fun selectByGenreAndLocationAndTime(genre: Int, location: String, time:Int) :List<String>

    @Query ("""Select id from playlists where location = :location and activity = :activity""")
    fun selectByLocationAndActivity(location: String, activity: Int) :List<String>

    @Query ("""Select id from playlists where genre = :genre
        and location = :location and activity = :activity""")
    fun selectByGenreAndLocationAndActivity(genre: Int, location: String, activity: Int) :List<String>

    @Query ("""Select id from playlists where
        location = :location and activity = :activity and time between :timeFrom and :timeTo """)
    fun selectByLocationAndActivityAndTime(location: String, activity: Int, timeFrom: Int, timeTo: Int) :List<String>

    @Query ("""Select id from playlists where genre = :genre and
        location = :location and activity = :activity and time between :timeFrom and :timeTo """)
    fun selectByGenreAndLocationAndActivityAndTime(genre: Int, location: String,
                                                   activity: Int, timeFrom: Int, timeTo: Int) :List<String>

}
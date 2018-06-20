package com.kalk.jmr

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.kalk.jmr.db.AppDatabase
import com.kalk.jmr.db.playlist.Playlist
import com.kalk.jmr.db.playlist.PlaylistDao
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlaylistDaoTests {
    private lateinit var db: AppDatabase
    private lateinit var dao: PlaylistDao

    private val playlists = arrayListOf(
            Playlist(1, "123", location = 1, activity = 2, genre = 3, time = 23),
            Playlist(2, "223", location = 2, activity = 2, genre = 3, time = 23),
            Playlist(3, "233", location = 2, activity = 3, genre = 3, time = 23)
    )

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dao = db.playlistDao()
    }

    @Test
    fun should_add_playlist_to_db() {
        val plist = Playlist(1, "asd", location = 1, activity = 1, genre = 1, time = 23)
        dao.addPlaylist(plist)
        assertEquals(dao.selectAll().size, 1)
    }

    @Test
    fun should_find_all_playlists_with_the_same_genre_id() {
        playlists.forEach(dao::addPlaylist)
        assertEquals(3, dao.selectByGenre(3).size)
    }

    @Test
    fun should_find_all_playlists_with_the_same_location_id() {
        playlists.forEach(dao::addPlaylist)
        assertEquals(2, dao.selectByLocation(2).size)
    }

    @Test
    fun should_find_all_playlists_with_the_same_time() {
        playlists.forEach(dao::addPlaylist)
        assertEquals(3, dao.selectByTime(23).size)
    }

    @Test
    fun should_find_all_playlists_with_the_same_activity_id() {
        playlists.forEach(dao::addPlaylist)
        assertEquals(2, dao.selectByActivity(2).size)
    }

    @Test
    fun should_find_all_playlists_with_the_same_genre_and_location_id() {
        playlists.forEach(dao::addPlaylist)
        assertEquals(2, dao.selectByGenreAndLocation(3, 2).size)
    }

    @Test
    fun should_find_all_playlists_with_the_same_genre__id_and_time() {
        playlists.forEach(dao::addPlaylist)
        assertEquals(3, dao.selectByGenreAndTime(3, 23).size)
    }

    @Test
    fun should_find_all_playlists_with_the_same_genre_and_activity_id() {
        playlists.forEach(dao::addPlaylist)
        assertEquals(1, dao.selectByGenreAndActivity(3, 3).size)
    }

    @Test
    fun should_find_all_playlists_with_the_same_genre_and_location_and_id_and_time() {
        playlists.forEach(dao::addPlaylist)
        assertEquals(2, dao.selectByGenreAndLocationAndTime(3, 2, 23).size)
    }

    @Test
    fun should_find_all_playlists_with_the_same_genre_and_location_and_activity_id() {
        playlists.forEach(dao::addPlaylist)
        assertEquals(1, dao.selectByGenreAndLocationAndActivity(3, 2, 2).size)
    }

    @Test
    fun should_find_all_playlists_with_the_same_genre_and_location_and_activity_id_and_time() {
        playlists.forEach(dao::addPlaylist)
        assertEquals(1, dao.selectByGenreAndLocationAndActivityAndTime(3, 2, 2, 23).size)
    }







}
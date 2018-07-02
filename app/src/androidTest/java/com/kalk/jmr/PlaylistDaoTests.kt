package com.kalk.jmr

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.kalk.jmr.db.AppDatabase
import com.kalk.jmr.db.playlist.Playlist
import com.kalk.jmr.db.playlist.PlaylistDao
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations


@RunWith(AndroidJUnit4::class)
class PlaylistDaoTests {
    private lateinit var db: AppDatabase
    private lateinit var dao: PlaylistDao
    @Mock private lateinit var observer: Observer<List<Playlist>>

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule() //we need this for testing live data


    private val playlists = arrayListOf(
            Playlist("UUID", "123", location = "UUID", activity = 2, genre = 3, time = 23),
            Playlist("UUID2", "223", location = "UUID2", activity = 2, genre = 3, time = 23),
            Playlist("UUID3", "233", location = "UUID2", activity = 3, genre = 3, time = 23)
    )

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dao = db.playlistDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun should_add_playlist_to_db() {
        val plist = Playlist("UUID", "asd", location = "UUID", activity = 1, genre = 1, time = 23)
        dao.addPlaylist(plist)
        val pl = dao.selectAll()
       // assertEquals(1, dao.selectAll())
    }


    @Test
    fun should_find_all_playlists_with_the_same_genre_id() {
        playlists.forEach(dao::addPlaylist)
        assertEquals(3, dao.selectByGenre(3).size)
    }

    @Test
    fun should_find_all_playlists_with_the_same_location_id() {
        playlists.forEach(dao::addPlaylist)
        assertEquals(2, dao.selectByLocation("UUID2").size)
    }

    @Test
    fun should_find_all_playlists_with_the_same_time() {
        playlists.forEach(dao::addPlaylist)
        assertEquals(3, dao.selectByTime(23-1, 23+1).size)
    }

    @Test
    fun should_find_all_playlists_with_the_same_activity_id() {
        playlists.forEach(dao::addPlaylist)
        assertEquals(2, dao.selectByActivity(2).size)
    }

    @Test
    fun should_find_all_playlists_with_the_same_genre_and_location_id() {
        playlists.forEach(dao::addPlaylist)
        assertEquals(2, dao.selectByGenreAndLocation(3, "UUID2").size)
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
        assertEquals(2, dao.selectByGenreAndLocationAndTime(3, "UUID2", 23).size)
    }

    @Test
    fun should_find_all_playlists_with_the_same_genre_and_location_and_activity_id() {
        playlists.forEach(dao::addPlaylist)
        assertEquals(1, dao.selectByGenreAndLocationAndActivity(3, "UUID2", 2).size)
    }

    @Test
    fun should_find_all_playlists_with_the_same_genre_and_location_and_activity_id_and_time() {
        playlists.forEach(dao::addPlaylist)
        assertEquals(1, dao.selectByGenreAndLocationAndActivityAndTime(3, "UUID2", 2, 23-1, 23+1).size)
    }







}
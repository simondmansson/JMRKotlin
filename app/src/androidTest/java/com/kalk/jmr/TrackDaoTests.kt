package com.kalk.jmr

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.kalk.jmr.db.AppDatabase
import com.kalk.jmr.db.track.Track
import com.kalk.jmr.db.track.TrackDao
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrackDaoTests {
    private lateinit var db: AppDatabase
    private lateinit var dao: TrackDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dao = db.trackDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun adds_a_track_to_the_db() {
        val track = Track("asd", "bb-dd")
        dao.addTrack(track)
        val queryResult = dao.byUri(track.uri)
        assertEquals( track.uri, queryResult.uri)
        assertEquals(track.name, queryResult.name)
    }

    @Test
    fun track_with_same_uri_as_existing_track_wont_be_added_to_db() {
        val track = Track("asd", "bb-dd")
        val track2 = Track("asd", "22-dd")
        dao.addTrack(track)
        dao.addTrack(track2)
        val queryResult = dao.byUri(track.uri)
        assertEquals(track.uri, queryResult.uri)
        assertEquals(track.name, queryResult.name)
        val queryResult2 = dao.byUri(track2.uri)
        assertEquals(track.uri, queryResult2.uri)
        assertEquals(track.name, queryResult2.name)
    }

}
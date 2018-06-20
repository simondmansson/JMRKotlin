package com.kalk.jmr

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.kalk.jmr.db.AppDatabase
import com.kalk.jmr.db.track.Track
import com.kalk.jmr.db.track.TrackDao
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

    @Test
    fun adds_a_track_to_the_db() {
        val track = Track(1, "asd", "bb-dd")
        dao.addTrack(track)
        val queryResult = dao.byId(track.id)
        assertEquals(queryResult.id, track.id)
        assertEquals(queryResult.uri, track.uri)
        assertEquals(queryResult.title, track.title)
    }

    @Test
    fun track_with_same_uri_as_existing_track_wont_be_added_to_db() {
        val track = Track(1, "asd", "bb-dd")
        val track2 = Track(2, "asd", "22-dd")
        dao.addTrack(track)
        dao.addTrack(track2)
        val queryResult = dao.byId(track.id)
        assertEquals(queryResult.id, track.id)
        assertEquals(queryResult.uri, track.uri)
        assertEquals(queryResult.title, track.title)
        val queryResult2 = dao.byId(track2.id)
        assertEquals(queryResult2, null)
    }
}